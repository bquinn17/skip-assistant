package com.github.cmb9400.skipassistant.controller;

import com.github.cmb9400.skipassistant.domain.SkippedTrackRepository;
import com.github.cmb9400.skipassistant.service.SpotifyHelperService;
import com.github.cmb9400.skipassistant.service.SpotifyPollingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class PageControllerImpl implements PageController {



    @Autowired
    SkippedTrackRepository skippedTrackRepository;

    @Autowired
    SpotifyHelperService spotifyHelperService;


    @Override
    public String index(Model model, HttpSession session) {
        if (session.getAttribute("api") == null) {
            String authURL = spotifyHelperService.getAuthorizationURL();
            model.addAttribute("loginURL", authURL);

            return "index";
        }
        else {
            model.addAttribute("user", session.getAttribute("user"));
            return "songList";
        }
    }


    @Override
    public String callback(@RequestParam(value="code", required=true) String code, Model model, HttpSession session) {
        try {
            SpotifyPollingService pollingService = spotifyHelperService.getNewPollingService(code);
            pollingService.init();

            session.setAttribute("user", pollingService.getUser().getId());
            session.setAttribute("api", pollingService.getApi());

            pollingService.run();
            return "redirect:/";
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
