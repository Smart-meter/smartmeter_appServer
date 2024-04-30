from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
import cv2
import numpy as np
from PIL import Image
from io import BytesIO
from ultralytics import YOLO
from transformers import TrOCRProcessor, VisionEncoderDecoderModel

app = FastAPI()

# Load YOLO model
model_path = 'last.pt'
model = YOLO(model_path)
threshold = 0.5

# Load TrOCR model and processor
processor = TrOCRProcessor.from_pretrained('microsoft/trocr-large-printed')
trocr_model = VisionEncoderDecoderModel.from_pretrained('microsoft/trocr-large-printed')

@app.get("/")
async def home():
    return {"message": "Home page"}

@app.post("/predict/")
async def predict(image: UploadFile = File(...)):
    try:
        contents = await image.read()
        img = Image.open(BytesIO(contents)).convert('RGB')
        img = np.array(img)

        # Perform object detection using YOLOv8
        results = model(img)[0]
        cropped_images = []

        detections = []

        for result in results.boxes.data.tolist():
            x1, y1, x2, y2, score, class_id = result

            if score > threshold:
                cv2.rectangle(img, (int(x1), int(y1)), (int(x2), int(y2)), (0, 255, 0), 4)
                text = "{} {:.2f}".format(results.names[int(class_id)].upper(), score)
                cv2.putText(img, text, (int(x1), int(y1 - 10)),
                            cv2.FONT_HERSHEY_SIMPLEX, 1.3, (0, 255, 0), 3, cv2.LINE_AA)

                # Crop the region of interest from the image
                roi = img[int(y1):int(y2), int(x1):int(x2)]
                cropped_images.append(roi)

                # Append detection information
                detections.append({
                    "coordinates": {"x1": int(x1), "y1": int(y1), "x2": int(x2), "y2": int(y2)},
                    "predicted_text": None 
                })

        # Process the cropped images with TrOCR
        texts = []
        for i, cropped_img in enumerate(cropped_images):
            cropped_pil_img = Image.fromarray(cropped_img)

            # Process the cropped image
            pixel_values = processor(images=cropped_pil_img, return_tensors="pt").pixel_values

            # Generate text from the cropped image
            generated_ids = trocr_model.generate(pixel_values)
            generated_text = processor.batch_decode(generated_ids, skip_special_tokens=True)[0]
            texts.append(generated_text)

             # Update detection with predicted text
            detections[i]["predicted_text"] = generated_text

        #return JSONResponse(content={"predicted_texts": texts})
        return JSONResponse(content={"detections": detections})
    
    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)
