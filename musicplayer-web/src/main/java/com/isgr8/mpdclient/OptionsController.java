package com.isgr8.mpdclient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class OptionsController {

    private MpdClient mpdClient;

    public OptionsController(MpdService mpdService) {
        this.mpdClient = mpdService.getMpdClient();
    }

    @GetMapping("status")
    Mono<MpdStatus> status() {
        return mpdClient.status();
    }

    @PutMapping("options/volume")
    void setVolume(int volume) {
        mpdClient.setVolume(volume);
    }

}
