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
    # Extracted meter reading logic
    extracted_meter_reading = 0
    for result in results.boxes.data.tolist():
        x1, y1, x2, y2, score, class_id = result
        if score > threshold:
            print("Score is more than threshold")
            # extracting text from the cropped region
            roi = img[int(y1):int(y2), int(x1):int(x2)]
            temp_path = os.path.join(upload_folder, 'temp_cropped.jpg')
            print("Saving the cropped image")
            cv2.imwrite(temp_path, roi)
            paddleresults = ocr.ocr(temp_path, cls=True)
            print('Paddle Results:-->', paddleresults)
            if paddleresults and len(paddleresults) > 0 and paddleresults[0] and len(paddleresults[0]) > 0 and \
                    paddleresults[0][0][1] and len(paddleresults[0][0][1]) > 0:
                extracted_meter_reading = paddleresults[0][0][1][0]
            else:
                extracted_meter_reading = ""

    # Return the extracted meter reading
    return {"meter_reading": extracted_meter_reading}
