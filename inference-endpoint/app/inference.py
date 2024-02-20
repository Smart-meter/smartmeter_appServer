from fastapi import FastAPI, File, UploadFile
from ultralytics import YOLO
from paddleocr import PaddleOCR
import os
import requests
import re
import boto3
import cv2
import numpy as np
from io import BytesIO
from PIL import Image

# AWS credentials
aws_access_key_id = "AKIA3K66FWVLPSQ56HM3"
aws_secret_access_key = "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"
aws_region = "us-east-2"

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
    #print('hiiiii')
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

    client = boto3.client('textract', region_name=aws_region, aws_access_key_id=aws_access_key_id, aws_secret_access_key=aws_secret_access_key)

    # Read the image file
    with open(file_path, "rb") as f:
        image_bytes = f.read()

    # Call Amazon Textract
    response = client.detect_document_text(
        Document={
            'Bytes': image_bytes
        }
    )
    #print(response)
    # Extract text from the response
    extracted_text = ""
    for item in response['Blocks']:
        if item['BlockType'] == 'LINE':
            extracted_text += item['Text'] + '\n'

    result = extracted_text.strip()
    print('meter')
    print(result)
    print('*')
    return re.findall(r'\b\d{5}\b', result)
