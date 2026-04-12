from flask import Flask, request, jsonify
import pickle
import numpy as np
import os
import json
from datetime import datetime
from werkzeug.utils import secure_filename

app = Flask(__name__)

# ─── Config ───────────────────────────────────────────────────
UPLOAD_FOLDER = "uploads"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
CONFIDENCE_THRESHOLD = 0.3

DISEASE_MODEL_PATH = "disease_model.keras"
CLASS_INDICES_PATH = "class_indices.json"

# ─── Lazy Model Loading (saves memory on startup) ─────────────
_crop_model = None
_crop_encoder = None
_disease_model = None
_disease_classes = None


def get_crop_model():
    global _crop_model
    if _crop_model is None:
        print("[INFO] Loading crop model...")
        with open("crop_model.pkl", "rb") as f:
            _crop_model = pickle.load(f)
        print("[INFO] Crop model loaded.")
    return _crop_model


def get_crop_encoder():
    global _crop_encoder
    if _crop_encoder is None:
        print("[INFO] Loading crop encoder...")
        with open("crop_encoder.pkl", "rb") as f:
            _crop_encoder = pickle.load(f)
        print("[INFO] Crop encoder loaded.")
    return _crop_encoder


def get_disease_model():
    global _disease_model
    if _disease_model is None:
        print("[INFO] Loading disease model (this may take a moment)...")
        from tensorflow.keras.models import load_model
        _disease_model = load_model(DISEASE_MODEL_PATH)
        print("[INFO] Disease model loaded.")
    return _disease_model


def get_disease_classes():
    global _disease_classes
    if _disease_classes is None:
        with open(CLASS_INDICES_PATH, "r") as f:
            class_indices = json.load(f)
        _disease_classes = {v: k for k, v in class_indices.items()}
    return _disease_classes


# ─── Helpers ──────────────────────────────────────────────────
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def preprocess_image(img_path, target_size=(224, 224)):
    from tensorflow.keras.preprocessing import image
    from tensorflow.keras.applications.mobilenet_v2 import preprocess_input

    if not os.path.exists(img_path):
        raise FileNotFoundError(f"File not found: {img_path}")

    img = image.load_img(img_path, target_size=target_size)
    if img.mode != "RGB":
        img = img.convert("RGB")

    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = preprocess_input(img_array)
    return img_array


# ─── Routes ───────────────────────────────────────────────────
@app.route("/", methods=["GET"])
def health_check():
    return jsonify({
        "status": "running",
        "message": "Flask ML API is operational",
        "endpoints": {
            "crop_prediction": "POST /api/predict-crop",
            "disease_detection": "POST /api/detect-disease"
        }
    }), 200


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"}), 200


@app.route("/api/predict-crop", methods=["POST"])
def predict_crop():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "No JSON data provided"}), 400

        required_fields = [
            "nitrogen", "phosphorus", "potassium",
            "temperature", "humidity", "ph", "rainfall"
        ]

        missing = [f for f in required_fields if f not in data]
        if missing:
            return jsonify({"error": f"Missing fields: {missing}"}), 400

        # Validate all values are numeric
        try:
            features = [float(data[f]) for f in required_fields]
        except (ValueError, TypeError):
            return jsonify({"error": "All fields must be numeric"}), 400

        model = get_crop_model()
        prediction = model.predict([features])[0]
        confidence = float(model.predict_proba([features]).max())

        return jsonify({
            "prediction": str(prediction),
            "confidence": round(confidence, 4),
            "generatedAt": datetime.now().isoformat()
        }), 200

    except Exception as e:
        print(f"[ERROR] predict_crop: {e}")
        return jsonify({"error": str(e)}), 500


@app.route("/api/detect-disease", methods=["POST"])
def detect_disease():
    filepath = None
    try:
        if 'file' not in request.files:
            return jsonify({"error": "No file part in request"}), 400

        file = request.files['file']

        if file.filename == '':
            return jsonify({"error": "No file selected"}), 400

        if not allowed_file(file.filename):
            return jsonify({
                "error": f"Invalid file type. Allowed: {ALLOWED_EXTENSIONS}"
            }), 400

        # Save file temporarily
        filename = secure_filename(file.filename)
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        unique_filename = f"{timestamp}_{filename}"
        filepath = os.path.join(UPLOAD_FOLDER, unique_filename)
        file.save(filepath)

        # Preprocess & predict
        img_array = preprocess_image(filepath)
        model = get_disease_model()
        disease_classes = get_disease_classes()

        predictions = model.predict(img_array)

        if predictions.shape[0] == 0:
            return jsonify({"error": "Model returned empty predictions"}), 500

        pred_probs = predictions[0]
        top_idx = int(np.argmax(pred_probs))
        top_confidence = float(pred_probs[top_idx])
        top_disease = disease_classes[top_idx]

        # All predictions above threshold
        detected = [
            {
                "diseaseName": disease_classes[idx],
                "confidence": round(float(prob), 4)
            }
            for idx, prob in enumerate(pred_probs)
            if prob >= CONFIDENCE_THRESHOLD
        ]
        detected.sort(key=lambda x: x['confidence'], reverse=True)

        return jsonify({
            "diseaseName": top_disease,
            "confidence": round(top_confidence, 4),
            "allPredictions": detected,
            "generatedAt": datetime.now().isoformat()
        }), 200

    except FileNotFoundError as e:
        return jsonify({"error": str(e)}), 404

    except Exception as e:
        print(f"[ERROR] detect_disease: {e}")
        return jsonify({"error": f"Prediction failed: {str(e)}"}), 500

    finally:
        # Always clean up uploaded file
        if filepath and os.path.exists(filepath):
            os.remove(filepath)


# ─── Main ─────────────────────────────────────────────────────
if __name__ == "__main__":
    print("=" * 50)
    print("  Flask ML API Starting...")
    print("  Endpoints:")
    print("    GET  /")
    print("    GET  /health")
    print("    POST /api/predict-crop")
    print("    POST /api/detect-disease")
    print("  Models load on first request (lazy loading)")
    print("=" * 50)
    app.run(debug=False, host='0.0.0.0', port=5000)