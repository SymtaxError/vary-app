package com.example.vary;

import com.squareup.moshi.Json;

public class Version {
        @Json(name = "version") int ver;

        public Version(int version) {
            ver = version;
        }
}
