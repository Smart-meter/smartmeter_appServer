from fastapi import FastAPI, File, UploadFile
from ultralytics import YOLO
from paddleocr import PaddleOCR
import cv2
import os
print("Import of libraries successful")
app = FastAPI()
# Initialize PaddleOCR
ocr = PaddleOCR(use_angle_cls=True, lang='en')
print("Initialization of PaddleOCR successful")
# Load the YOLO model
model_path = 'last.pt'
model = YOLO(model_path)  # load a custom model
threshold = 0.5
print("model loaded succeesfully")

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    upload_folder = "temp"
    os.makedirs(upload_folder, exist_ok=True)  # Create the folder if it doesn't exist
    file_path = os.path.join(upload_folder, file.filename)
    print("Saving uploaded image to:", file_path)
    with open(file_path, "wb") as image:
        image.write(file.file.read())
    print("Input file saved successfully into the temp folder")
    # Perform inference
    print("File Path:", file_path)
    img = cv2.imread(file_path)
    results = model(img)[0]
    extracted_meter_reading = 0
    # Return the extracted meter reading
    return {"meter_reading": extracted_meter_reading}
