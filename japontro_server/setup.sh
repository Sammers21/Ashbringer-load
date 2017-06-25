#!/usr/bin/env bash
docker run -p 8080:8080 -v $(pwd)/hello.py:/hello.py japronto/japronto --script /hello.py