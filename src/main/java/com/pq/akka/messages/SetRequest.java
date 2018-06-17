package com.pq.akka.messages;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SetRequest {
    private final String key;
    private final Object value;
}
