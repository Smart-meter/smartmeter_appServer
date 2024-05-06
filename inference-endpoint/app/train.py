import boto3
from ultralytics import YOLO
import os
import random

# AWS S3 Setup
s3 = boto3.client('s3',
    aws_access_key_id='',
    aws_secret_access_key='',
    region_name='us-east-1' 
)

# Set the seed for reproducibility
random.seed(42)

# Define the base directories for storing split datasets
local_base_dir = '/home/metertrain/dataset'
images_base = os.path.join(local_base_dir, 'images')
labels_base = os.path.join(local_base_dir, 'labels')

# Create directories for images and labels in train, validation, and test sets
for kind in ['train', 'val', 'test']:
    os.makedirs(os.path.join(images_base, kind), exist_ok=True)
    os.makedirs(os.path.join(labels_base, kind), exist_ok=True)

# Temporary directories for downloading files
local_temp_images = '/tmp/images'
local_temp_labels = '/tmp/labels'
os.makedirs(local_temp_images, exist_ok=True)
os.makedirs(local_temp_labels, exist_ok=True)

bucket_name = 'newmeterdata'  # S3 bucket name

# Gather all image files from S3
all_images = []
paginator = s3.get_paginator('list_objects_v2')
pages = paginator.paginate(Bucket=bucket_name, Prefix='images/')

for page in pages:
    for obj in page.get('Contents', []):
        file_name = obj['Key'].split('/')[-1]
        if file_name.endswith('.png'):
            all_images.append(file_name)

# Shuffle the image files
random.shuffle(all_images)

# Calculate split indices
num_images = len(all_images)
train_index = int(num_images * 0.7)  # 70% for training
valid_index = int(num_images * 0.9)  # 20% for validation, remaining 10% for testing

# Split the images into sets
train_images = all_images[:train_index]
valid_images = all_images[train_index:valid_index]
test_images = all_images[valid_index:]

# Function to download and organize files into the local dataset folder
def process_files(files, folder):
    for file in files:
        image_src = os.path.join(local_temp_images, file)
        label_src = os.path.join(local_temp_labels, file.replace('.png', '.txt'))
        
        # Download image and label from S3
        s3.download_file(bucket_name, f'images/{file}', image_src)
        s3.download_file(bucket_name, f'labels/{file.replace(".png", ".txt")}', label_src)
        
        # Define destination paths
        image_dest = os.path.join(images_base, folder, file.replace('.png', '.jpg'))
        label_dest = os.path.join(labels_base, folder, file.replace('.png', '.txt'))
        
        # Move files to the respective directories
        os.rename(image_src, image_dest)
        os.rename(label_src, label_dest)

# Process files for each dataset partition
process_files(train_images, 'train')
process_files(valid_images, 'val')
process_files(test_images, 'test')

print('Dataset has been downloaded and organized into local folders.')

def get_latest_model_version(bucket, prefix):
    """Retrieve the latest model version directory from an S3 bucket."""
    paginator = s3.get_paginator('list_objects_v2')
    pages = paginator.paginate(Bucket=bucket, Prefix=prefix, Delimiter='/')

    latest_version = None
    for page in pages:
        prefixes = sorted([p['Prefix'] for p in page.get('CommonPrefixes', [])], reverse=True)
        if prefixes:
            latest_version = prefixes[0]
            return latest_version + 'weights/model.pt'
    return None

def download_model(s3_bucket, s3_object_key, local_file_path):
    """Download a model file from S3 to a local path."""
    s3.download_file(Bucket=s3_bucket, Key=s3_object_key, Filename=local_file_path)
    print(f"Downloaded {s3_object_key} from S3 bucket {s3_bucket} to {local_file_path}")

# Example usage:
bucket_name = 'meter_detection_model'
prefix = 'training_artifacts/'

latest_model_key = get_latest_model_version(bucket_name, prefix)
local_model_path = '/path/to/local/model.pt'

if latest_model_key:
    try:
        download_model(bucket_name, latest_model_key, local_model_path)
        model = YOLO("yolov8n.yaml").load(local_model_path)
        print("Model loaded from the latest available artifact.")
    except Exception as e:
        print("Failed to download or load model:", str(e))
        model = YOLO("yolov8n.yaml")
        print("Initialized a new model due to download or load failure.")
else:
    model = YOLO("yolov8n.yaml")
    print("No previous artifacts found. Initialized a new model.")

#Load a model
# model = YOLO("yolov8n.yaml").load() 
#Build a new model from scratch

#use the model
results = model.train(data="/home/metertrain/dataset/config.yaml"), epochs=20) #train the model


# Function to upload a directory to S3
def upload_directory_to_s3(local_path, s3_bucket, s3_path):
    for dirpath, dirnames, filenames in os.walk(local_path):
        for filename in filenames:
            local_file_path = os.path.join(dirpath, filename)
            relative_path = os.path.relpath(local_file_path, local_path)
            s3_file_path = os.path.join(s3_path, relative_path)
            s3.upload_file(local_file_path, s3_bucket, s3_file_path)
            print(f"Uploaded {local_file_path} to s3://{s3_bucket}/{s3_file_path}")

# Upload model artifacts and weights
artifact_base_path = '/home/metertrain/runs/detect/train'
timestamp = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
s3_artifact_path = f"training_artifacts/{timestamp}/"
upload_directory_to_s3(artifact_base_path, bucket_name, s3_artifact_path)

print(f"All training artifacts and weights have been uploaded to {bucket_name} under {s3_artifact_path}")


