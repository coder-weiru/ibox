# iplanner-api (Serverless API using AWS SAM)

## Requirements

* [AWS CLI installed and configured](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
* [Java SE Development Kit 8 installed](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Docker installed](https://www.docker.com/community-edition)
* [Maven](https://maven.apache.org/install.html)
* [SAM CLI](https://github.com/awslabs/aws-sam-cli)

### Local development

**Invoking function locally through local API Gateway**
1. Start DynamoDB Local in a Docker container. `docker run -p 8000:8000 amazon/dynamodb-local`
2. Start the SAM local API.
```bash
sam local start-api --template template.yaml
```

If the previous command ran successfully you should now be able to hit the following local endpoint to
invoke the functions rooted at `http://localhost:3000/{api.service.contextRoot}`

## Packaging and deployment to AWS
```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket $BUCKET_NAME
```

Next, the following command will create a Cloudformation Stack and deploy your SAM resources.

```bash
sam deploy \
    --template-file packaged.yaml \
    --stack-name $STACK_NAME \
    --capabilities CAPABILITY_IAM
```

After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

```bash
aws cloudformation describe-stacks \
    --stack-name $STACK_NAME \
    --query 'Stacks[].Outputs'
```
Or:

```bash
aws cloudformation describe-stack-resources \
    --stack-name $STACK_NAME \
```

Finally to clean Up

```bash
aws cloudformation delete-stack \
    --stack-name $STACK_NAME \
```

## Testing
Import the postman collections from the test/resources/postman folder to invoke the endpoints
 
# Appendix

## AWS CLI commands

AWS CLI commands to package, deploy and describe outputs defined within the cloudformation stack:

```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket ibox-sam-bucket

sam deploy \
    --template-file packaged.yaml \
    --stack-name iplanner-api-sam-stack \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides MyParameterSample=MySampleValue

aws cloudformation describe-stacks \
    --stack-name iplanner-api-sam-stack --query 'Stacks[].Outputs'

aws cloudformation delete-stack \
    --stack-name iplanner-api-sam-stack \
```
