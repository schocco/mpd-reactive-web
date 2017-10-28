package com.isgr8.mpdclient;

import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

class ResponseParser {

    private ResponseParser() {}

    static Map<String, String> toMap(String mpdResponse) {
        return Arrays.stream(mpdResponse.split("\n"))
                .map(line -> line.split(": ", 2))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    static Flux<String> splitResponse(Flux<String> response, String field) {
        return response.reduce(new ArrayList<>(), reduceToStringJoiners(field))
                .flatMapIterable(stringJoiners -> stringJoiners)
                .map(StringJoiner::toString);
    }

    private static BiFunction<List<StringJoiner>, String, List<StringJoiner>> reduceToStringJoiners(String field) {
        return (joiners, line) -> {
            StringJoiner joiner;
            if(line.startsWith(field)) {
                joiner = new StringJoiner("\n");
                joiners.add(joiner);
            } else {
                joiner = joiners.get(joiners.size() -1);
            }
            joiner.add(line);
            return joiners;
        };
    }
}
