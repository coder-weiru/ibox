from __future__ import print_function
from boto3 import resource
import json

dynamodb_resource = resource('dynamodb')
TABLE = "iplanner_events"

print('Loading function')

def add_event_handler(event, context):
    print("Got event\n" + json.dumps(event, indent=2))
