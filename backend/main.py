import asyncio
import joblib
import pandas as pd
from fastapi import FastAPI, HTTPException
from fastapi.concurrency import run_in_threadpool
from pydantic import BaseModel
import tldextract

# Import the new feature extractor from Phishing_detection_Project
from feature_extractor import extract_url_features

# Import model, extraction engine, and domain rules from predict_url.py (old rules)
from predict_url import (
    EXACT_LEGITIMATE_DOMAINS,
    TRUSTED_TLD_SUFFIXES
)

# Import the new model and feature names
MODEL_PATH = "model_new/mlp_phishing_pipeline.pkl"
FEATURE_PATH = "model_new/feature_names.pkl"

new_model = joblib.load(MODEL_PATH)
new_feature_names = joblib.load(FEATURE_PATH)

# Import the auth router
from auth import router as auth_router

app = FastAPI(title="SafeGuard AI API")

# Include the auth router
app.include_router(auth_router)

class URLRequest(BaseModel):
    url: str

def clean_and_normalize_url(raw_url: str) -> str:
    url = raw_url.strip()
    if not url.startswith(("http://", "https://")):
        url = "http://" + url
    return url

@app.get("/")
def home():
    return {
        "status": "SafeGuard AI Backend Running",
        "model": "MLP (Updated)",
        "features": len(new_feature_names)
    }

@app.post("/predict")
async def predict_phishing(request: URLRequest):
    raw_input = request.url.strip()
    if not raw_input:
        raise HTTPException(status_code=400, detail="URL cannot be empty")
    
    url = clean_and_normalize_url(raw_input)

    try:
        ext = tldextract.extract(url)
        registered_domain = f"{ext.domain}.{ext.suffix}".lower() if ext.suffix else ext.domain.lower()

        # Rule 1: Exact match for verified legitimate domains
        if registered_domain in EXACT_LEGITIMATE_DOMAINS and ext.suffix and not ext.subdomain:
            return {
                "url": url,
                "status": "SAFE",
                "prediction": "LEGITIMATE",
                "risk_score": 0.0,
                "confidence_legitimate": 100.0,
                "confidence_phishing": 0.0,
                "legitimate_probability": 1.0,
                "phishing_probability": 0.0,
                "security_checks": {
                    "https_enabled": bool(url.startswith("https://")),
                    "trusted_domain": True,
                    "no_suspicious_redirect": True,
                    "clean_url_structure": True
                }
            }

        # Rule 2: Official Educational and Government domains (.edu.np, .gov.np, etc.)
        if ext.suffix in TRUSTED_TLD_SUFFIXES and not ext.subdomain:
            return {
                "url": url,
                "status": "SAFE",
                "prediction": "LEGITIMATE",
                "risk_score": 0.0,
                "confidence_legitimate": 100.0,
                "confidence_phishing": 0.0,
                "legitimate_probability": 1.0,
                "phishing_probability": 0.0,
                "security_checks": {
                    "https_enabled": bool(url.startswith("https://")),
                    "trusted_domain": True,
                    "no_suspicious_redirect": True,
                    "clean_url_structure": True
                }
            }

        # Rule 3: Invalid Top-Level Domain
        if not bool(ext.suffix):
            return {
                "url": url,
                "status": "PHISHING",
                "prediction": "PHISHING",
                "risk_score": 100.0,
                "confidence_legitimate": 0.0,
                "confidence_phishing": 100.0,
                "legitimate_probability": 0.0,
                "phishing_probability": 1.0,
                "security_checks": {
                    "https_enabled": bool(url.startswith("https://")),
                    "trusted_domain": False,
                    "no_suspicious_redirect": False,
                    "clean_url_structure": False
                }
            }

        # Rule 4: Machine Learning Pipeline with 10s Timeout Guard (Feature extraction can take time)
        try:
            features = await asyncio.wait_for(
                run_in_threadpool(extract_url_features, url),
                timeout=10.0
            )

            # Arrange features in EXACT training order
            X = pd.DataFrame(
                [[
                    features[name]
                    for name in new_feature_names
                ]],
                columns=new_feature_names
            )

            pred = new_model.predict(X)[0]
            prob = new_model.predict_proba(X)[0]

            conf_legit = round(float(prob[0]) * 100, 2)
            conf_phish = round(float(prob[1]) * 100, 2)

            label = "SAFE" if pred == 0 else "PHISHING"
            prediction_text = "LEGITIMATE" if pred == 0 else "PHISHING"

            return {
                "url": url,
                "status": label,
                "prediction": prediction_text,
                "risk_score": conf_phish,
                "confidence_legitimate": conf_legit,
                "confidence_phishing": conf_phish,
                "legitimate_probability": round(float(prob[0]), 4),
                "phishing_probability": round(float(prob[1]), 4),
                "security_checks": {
                    "https_enabled": bool(url.startswith("https://")),
                    "trusted_domain": bool(pred == 0),
                    "no_suspicious_redirect": bool(not ("redirect=" in url or "@" in url)),
                    "clean_url_structure": bool(url.count('-') < 3 and url.count('.') < 5)
                }
            }

        except asyncio.TimeoutError:
            print(f"[Timeout Guard] Feature extraction took too long for {url}.")
            raise HTTPException(status_code=504, detail="Analysis timed out")

    except Exception as e:
        print(f"[Error] {url}: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))
