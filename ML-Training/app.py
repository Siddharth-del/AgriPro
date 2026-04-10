from flask import Flask, request, jsonify
import pickle
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image
import numpy as np
import os
import json
from datetime import datetime
from werkzeug.utils import secure_filename

app = Flask(__name__)

UPLOAD_FOLDER = "uploads"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}

with open("crop_model.pkl", "rb") as f:
    crop_model = pickle.load(f)

DISEASE_MODEL_PATH = "disease_model.keras"
CLASS_INDICES_PATH = "class_indices.json"

disease_model = load_model(DISEASE_MODEL_PATH)

with open(CLASS_INDICES_PATH, "r") as f:
    class_indices = json.load(f)

DISEASE_CLASSES = {v: k for k, v in class_indices.items()}

CONFIDENCE_THRESHOLD = 0.3

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def preprocess_image(img_path, target_size=(224, 224)):
    if not os.path.exists(img_path):
        raise FileNotFoundError(f"File not found: {img_path}")
    img = image.load_img(img_path, target_size=target_size)
    if img.mode != "RGB":
        img = img.convert("RGB")
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    from tensorflow.keras.applications.mobilenet_v2 import preprocess_input
    img_array = preprocess_input(img_array)
    return img_array

@app.route("/", methods=["GET"])
def health_check():
    return jsonify({
        "status": "running",
        "message": "Flask ML API is operational",
        "endpoints": {
            "crop": "/api/predict-crop",
            "disease": "/api/detect-disease"
        }
    })

@app.route("/api/predict-crop", methods=["POST"])
def predict_crop():
    try:
        data = request.json
        if not data:
            return jsonify({"error": "No JSON data provided"}), 400
        required_fields = ["nitrogen", "phosphorus", "potassium", 
                         "temperature", "humidity", "ph", "rainfall"]
        for field in required_fields:
            if field not in data:
                return jsonify({"error": f"{field} is required"}), 400
        features = [data[field] for field in required_fields]
        prediction = crop_model.predict([features])[0]
        confidence = float(crop_model.predict_proba([features]).max())
        return jsonify({
            "prediction": prediction,
            "confidence": confidence,
            "generatedAt": datetime.now().isoformat()
        }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/detect-disease", methods=["POST"])
def detect_disease():
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
        filename = secure_filename(file.filename)
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        unique_filename = f"{timestamp}_{filename}"
        filepath = os.path.join(UPLOAD_FOLDER, unique_filename)
        file.save(filepath)
        try:
            img_array = preprocess_image(filepath)
            predictions = disease_model.predict(img_array)
            if predictions.shape[0] == 0:
                return jsonify({"error": "Model returned empty predictions"}), 500
            pred_probs = predictions[0]
            top_idx = np.argmax(pred_probs)
            top_confidence = float(pred_probs[top_idx])
            top_disease = DISEASE_CLASSES[top_idx]
            detected = []
            for idx, prob in enumerate(pred_probs):
                if prob >= CONFIDENCE_THRESHOLD:
                    detected.append({
                        "diseaseName": DISEASE_CLASSES[idx],
                        "confidence": float(prob)
                    })
            detected.sort(key=lambda x: x['confidence'], reverse=True)
            response = {
                "diseaseName": top_disease,
                "confidence": top_confidence,
                "allPredictions": detected,
                "generatedAt": datetime.now().isoformat()
            }
            return jsonify(response), 200
        finally:
            if os.path.exists(filepath):
                os.remove(filepath)
    except FileNotFoundError as fnf:
        return jsonify({"error": str(fnf)}), 404
    except Exception as e:
        return jsonify({"error": f"Prediction failed: {str(e)}"}), 500

if __name__ == "__main__":
    print("=" * 50)
    print("Flask ML API Starting...")
    print(f"Disease Model: {DISEASE_MODEL_PATH}")
    print(f"Class Indices: {CLASS_INDICES_PATH}")
    print(f"Number of classes: {len(DISEASE_CLASSES)}")
    print("=" * 50)
    app.run(debug=True, host='0.0.0.0', port=5000)
