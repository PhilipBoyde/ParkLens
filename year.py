import requests
import json

start_year = 2025
end_year = 2050
red_days = []
base_url = "http://sholiday.faboul.se/dagar/v2.1/"

for year in range(start_year, end_year + 1):
    url = f"{base_url}{year}"
    response = requests.get(url)

    if response.status_code == 200:
        data = response.json()
        for day in data.get("dagar", []):
            if day.get("röd dag", "").lower() == "ja":
                holiday_name = day.get("helgdag", "").strip()
                if holiday_name and holiday_name.lower() != "söndag":
                    red_days.append({
                        "date": day["datum"],
                        "name": holiday_name
                    })
    else:
        print(f"Failed to fetch data for year {year}: {response.status_code}")

with open("swedish_red_days_2025_2050.json", "w", encoding="utf-8") as f:
    json.dump(red_days, f, ensure_ascii=False, indent=2)

print("Done")