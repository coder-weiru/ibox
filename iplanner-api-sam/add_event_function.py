from __future__ import print_function
from boto3 import resource
import json


class MissingRequiredElementException(Exception):
    pass


dynamodb_resource = resource('dynamodb')

event_table = dynamodb_resource.Table("iplanner_events")


def lambda_handler(event: object, context: object) -> object:
    print("Got event\n" + json.dumps(event, indent=2))

    if event['body']:
        json_array = event['body']

        for item in json_array:
            add_event(item)

    return True


def add_event(data):
    print("add_event request: %s" % data)

    if data['id'] is None or data['summary'] is None or data['creator'] is None or data['start'] is None:
        raise MissingRequiredElementException('Missing key attrubte values for this event')

    response = event_table.put_item(Item={
        'Id': data.get('id', 1),
        'Summary': data.get('summary', 'Default Summary'),
        'Description': data.get('description', 'Default Description'),
        'Creator': data.get('creator').get('id'),
        'Creator.Info': data.get('creator', {
            "id": "unknown",
            "email": "unknown",
            "displayName": "unknown",
            "self": False
        }),
        'Created': data.get('created', '1995-09-07 10:40:52'),
        'Updated': data.get('updated', '1995-09-07 10:40:52'),
        'Start': data.get('start', '1995-09-07 10:40:52'),
        'End': data.get('end', '1995-09-07 10:40:52'),
        'Activity': data.get('activity', 'Default'),
        'Status': data.get('status', 'S'),
        'Location': data.get('location', 'Default Location'),
        'EndTimeUnspecified': data.get('endTimeUnspecified', False),
        'Recurrence': data.get('recurrence', [])
    })

    return response
