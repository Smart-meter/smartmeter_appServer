# This is the inference endpoint module

## Iteration 1
We are building a /predict endpoint which takes in an image file
and returns the meter reading detected out of it.
The endpoint is built using FastAPI.

Ensure that all dependencies are installed from requirements.txt
pip install --no-cache-dir -r ./requirements.txt from the inference-endpoint folder
Go to app folder

Run uvicorn inference:app --log-level debug --port 8001

The Swagger APIs can be seen and tested at  http://127.0.0.1:8001/docs