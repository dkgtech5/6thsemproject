from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import pandas as pd
import tldextract
from feature_extractor import extract_url_features

# Import the auth router
from auth import router as auth_router

# ============================================================
# FastAPI Application
# ============================================================

app = FastAPI(
    title="AI-Based Phishing Detection API",
    description="MLP-based phishing website detection API",
    version="1.0"
)

# Include the auth router
app.include_router(auth_router)

# ============================================================
# Load Model
# ============================================================

MODEL_PATH = "model/mlp_phishing_pipeline.pkl"
FEATURE_PATH = "model/feature_names.pkl"

model = joblib.load(MODEL_PATH)
feature_names = joblib.load(FEATURE_PATH)

# ============================================================
# Heuristic Rules & Constants
# ============================================================

EXACT_LEGITIMATE_DOMAINS = {
    'google.com', 'github.com', 'amazon.com', 'facebook.com',
    'paypal.com', 'microsoft.com', 'apple.com', 'youtube.com',
    'ncit.edu.np', 'tu.edu.np', 'ku.edu.np', 'pu.edu.np', 'esewa.com.np', 'khalti.com'
}

TRUSTED_TLD_SUFFIXES = {'edu.np', 'gov.np', 'ac.uk', 'edu', 'gov'}

# ============================================================
# Request Model
# ============================================================

class URLRequest(BaseModel):
    url: str

# ============================================================
# Health Check
# ============================================================

@app.get("/")
def home():
    return {
        "status": "online",
        "message": "AI-Based Phishing Detection API",
        "model": "MLP (Updated)",
        "features": len(feature_names)
    }

# ============================================================
# Prediction Endpoint
# ============================================================

@app.post("/predict")
def predict(request: URLRequest):
    try:
        url = request.url.strip()
        if not url.startswith(("http://", "https://")):
            url = "http://" + url

        ext = tldextract.extract(url)
        registered_domain = f"{ext.domain}.{ext.suffix}".lower() if ext.suffix else ext.domain.lower()

        # --- HEURISTIC CHECK 1: Whitelist ---
        if registered_domain in EXACT_LEGITIMATE_DOMAINS and not ext.subdomain:
            return create_response(url, "SAFE", 0.0, 1.0, 0.0, True)

        # --- HEURISTIC CHECK 2: Trusted Suffixes ---
        if ext.suffix in TRUSTED_TLD_SUFFIXES and not ext.subdomain:
            return create_response(url, "SAFE", 0.0, 1.0, 0.0, True)

        # --- ML MODEL CHECK ---
        try:
            features = extract_url_features(url)
        except Exception as e:
            print(f"Extraction error for {url}: {e}")
            # Fallback: identify as PHISHING if technical scan crashes on a suspicious link
            return create_response(url, "PHISHING", 95.0, 0.05, 0.95, False)

        # Ensure all required features are present, using 0 as a default for missing ones
        X = pd.DataFrame(
            [[features.get(name, 0) for name in feature_names]],
            columns=feature_names
        )

        prediction = model.predict(X)[0]
        probabilities = model.predict_proba(X)[0]

        status = "PHISHING" if prediction == 1 else "SAFE"
        risk_score = round(float(probabilities[1]) * 100, 2)

        return create_response(
            url,
            status,
            risk_score,
            probabilities[0],
            probabilities[1],
            prediction == 0
        )

    except HTTPException:
        raise
    except Exception as e:
        print(f"Prediction Error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

def create_response(url, status, risk_score, prob_legit, prob_phish, is_trusted):
    return {
        "url": url,
        "status": status,
        "prediction": "LEGITIMATE" if status == "SAFE" else "PHISHING",
        "risk_score": risk_score,
        "confidence_legitimate": round(float(prob_legit) * 100, 2),
        "confidence_phishing": round(float(prob_phish) * 100, 2),
        "legitimate_probability": round(float(prob_legit), 4),
        "phishing_probability": round(float(prob_phish), 4),
        "security_checks": {
            "https_enabled": url.startswith("https://"),
            "trusted_domain": is_trusted,
            "no_suspicious_redirect": not ("redirect=" in url or "@" in url),
            "clean_url_structure": url.count('-') < 3 and url.count('.') < 5
        }
    }
