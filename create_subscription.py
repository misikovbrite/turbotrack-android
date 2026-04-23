#!/usr/bin/env python3
"""Create and activate turbotrack_weekly subscription in Google Play Console."""

import json
from google.oauth2 import service_account
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

KEY_FILE = "/Users/m/Downloads/brite-ads-automation-33f3693602ca.json"
PACKAGE = "com.britetodo.turbotrack"
PRODUCT_ID = "turbotrack_weekly"
BASE_PLAN_ID = "weekly"
SCOPES = ["https://www.googleapis.com/auth/androidpublisher"]
REGIONS_VERSION = "2022/02"

def get_service():
    creds = service_account.Credentials.from_service_account_file(KEY_FILE, scopes=SCOPES)
    return build("androidpublisher", "v3", credentials=creds)

def create_subscription(service):
    body = {
        "productId": PRODUCT_ID,
        "packageName": PACKAGE,
        "listings": [
            {
                "languageCode": "en-US",
                "title": "TurboTrack Premium",
                "description": "Unlimited turbulence forecasts, real-time PIREP maps, and advanced aviation weather data.",
                "benefits": [
                    "Unlimited flight forecasts",
                    "Real-time turbulence maps",
                    "Pilot reports (PIREPs)",
                    "Advanced weather data"
                ]
            }
        ],
        "basePlans": [
            {
                "basePlanId": BASE_PLAN_ID,
                "autoRenewingBasePlanType": {
                    "billingPeriodDuration": "P1W",
                    "gracePeriodDuration": "P3D",
                    "resubscribeState": "RESUBSCRIBE_STATE_ACTIVE",
                    "legacyCompatible": True
                },
                "regionalConfigs": [
                    {
                        "regionCode": "US",
                        "newSubscriberAvailability": True,
                        "price": {
                            "currencyCode": "USD",
                            "units": "2",
                            "nanos": 990000000
                        }
                    }
                ],
                "otherRegionsConfig": {
                    "usdPrice": {"currencyCode": "USD", "units": "2", "nanos": 990000000},
                    "eurPrice": {"currencyCode": "EUR", "units": "2", "nanos": 790000000},
                    "newSubscriberAvailability": True
                }
            }
        ]
    }

    try:
        result = service.monetization().subscriptions().create(
            packageName=PACKAGE,
            productId=PRODUCT_ID,
            regionsVersion_version=REGIONS_VERSION,
            body=body
        ).execute()
        print("✅ Subscription created (base plan in DRAFT):")
        print(json.dumps(result, indent=2))
        return True
    except HttpError as e:
        error_content = json.loads(e.content.decode())
        code = error_content.get("error", {}).get("code")
        message = error_content.get("error", {}).get("message", str(e))
        if code == 409 or "already exists" in message.lower():
            print(f"⚠️  Subscription '{PRODUCT_ID}' already exists — continuing to activate...")
            return True
        print(f"❌ Create error {code}: {message}")
        raise

def activate_base_plan(service):
    try:
        result = service.monetization().subscriptions().basePlans().activate(
            packageName=PACKAGE,
            productId=PRODUCT_ID,
            basePlanId=BASE_PLAN_ID,
            body={}
        ).execute()
        print(f"✅ Base plan '{BASE_PLAN_ID}' activated:")
        state = result.get("basePlans", [{}])[0].get("state", "unknown") if result.get("basePlans") else "unknown"
        print(f"   State: {state}")
        return result
    except HttpError as e:
        error_content = json.loads(e.content.decode())
        code = error_content.get("error", {}).get("code")
        message = error_content.get("error", {}).get("message", str(e))
        print(f"❌ Activate error {code}: {message}")
        raise

def main():
    print(f"Creating subscription '{PRODUCT_ID}' for {PACKAGE}...\n")
    service = get_service()

    if create_subscription(service):
        print("\nActivating base plan...")
        result = activate_base_plan(service)
        print("\n🎉 Done! Subscription is live in Google Play.")
        print(f"   Product ID: {PRODUCT_ID}")
        print(f"   Base plan:  {BASE_PLAN_ID}")
        print(f"   Price:      $2.99/week")

if __name__ == "__main__":
    main()
