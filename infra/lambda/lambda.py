import json
import boto3
from datetime import datetime, timedelta

s3 = boto3.client('s3', endpoint_url='http://s3.localhost.localstack.cloud:4566')

def handler(event, context):
    print("Received event:")
    print(json.dumps(event, indent=2))

    for record in event['Records']:
        body = json.loads(record['body'])
        lines = []

        timestamp_str = body.get('timestamp', '')
        if timestamp_str:
            utc_time = datetime.fromisoformat(timestamp_str)
            uk_time = utc_time + timedelta(hours=1)
            formatted_time = uk_time.strftime('%d %B %Y, %H:%M')
        else:
            formatted_time = 'Unknown Date'

        lines.append(f"Workout Summary: {body.get('name', 'Unnamed')}")
        lines.append(f"Date: {formatted_time}")
        lines.append(f"Duration: {body.get('durationInMinutes', 0)} minutes")
        lines.append("\nExercises:")

        for exercise in body.get('exercises', []):
            lines.append(f"  - {exercise['name']} ({exercise['primaryMuscleGroup']})")
            for idx, s in enumerate(exercise.get('sets', []), start=1):
                lines.append(f"     Set {idx}: {s['reps']} reps × {s['weight']} kg")

        prs = body.get('personalRecords', [])
        if prs:
            lines.append("\n🏆 Personal Records:")
            for pr in prs:
                one_rm = pr.get('oneRepMax', 0)
                lines.append(f"  - {pr['exerciseTemplate']['name']}: {one_rm:.1f} kg")

        summary_text = "\n".join(lines)

        try:
            print(f"Uploading summary for log ID {body['id']} to S3...")
            s3.put_object(
                Bucket='report-bucket',
                Key=f"workout-summaries/log-{body['id']}.txt",
                Body=summary_text.encode('utf-8')
            )
            print(f"Successfully uploaded log-{body['id']}.txt to S3.")
        except Exception as e:
            print(f"Failed to upload to S3: {str(e)}")