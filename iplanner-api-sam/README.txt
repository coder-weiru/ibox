### iplanner-functions

##Instructions

#Build Distribution Artifact



#Packaging
aws cloudformation package --template-file ./sam-templates/template-inline-swagger.yaml --s3-bucket ibox-sam-bucket --output-template-file ./sam-templates/packaged-template.yml

#Deploying
aws cloudformation deploy --template-file ./sam-templates/packaged-template.yml --stack-name iplanner-api-sam-stack  --capabilities CAPABILITY_IAM

#Review Deployment
aws cloudformation describe-stack-resources --stack-name iplanner-api-sam-stack

#Clean Up
aws cloudformation delete-stack --stack-name iplanner-api-sam-stack