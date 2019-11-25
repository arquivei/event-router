#!/bin/bash

# --- FILL HERE BEFORE CONTINUE ---
gcp_project=GCP_PROJECT
# --- END--- 

project_name="event-router"
image_base_name="gcr.io/$gcp_project/$project_name"

month=`date +%m`
year_2d=`date +%y`
year_4d=`date +%Y`

number_of_images=`gcloud container images list-tags $image_base_name --project $gcp_project \
--filter="timestamp.year=$year_4d AND timestamp.month=$month" --format "list[compact]" | wc -l`

build_number=`expr $number_of_images + 1`
tag="$year_2d$month.$build_number"

echo "building version $tag"
sed -i "s/version *in *Docker *:= *\".*\"/version in Docker := \"$tag\"/g" build.sbt

sbt Docker/publish