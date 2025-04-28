#!/bin/zsh

set -e

echo "Cleaning old lambda package"
rm -rf lambda-package
mkdir lambda-package

echo "Copying lambda.py"
cp lambda.py lambda-package/

echo "Installing boto3"
pip3 install boto3 --target lambda-package/

echo "Zipping the package"
cd lambda-package
zip -r ../function.zip .
cd ..

echo "Uploading zip to S3"
if ! aws --endpoint-url=http://localhost:4566 s3 cp function.zip s3://report-bucket/lambda/function.zip; then
  echo "❌ Upload to S3 failed. Abort"
  exit 1
fi

echo "Updating stack"
awslocal --endpoint-url=http://localhost:4566 cloudformation update-stack --stack-name lambda --template-body file://../cloudformation/lambda.yml

echo "Lambda updated successfully"