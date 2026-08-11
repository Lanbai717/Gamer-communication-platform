package com.example.badminton_team.controller.user;

import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Venue;
import com.example.badminton_team.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/user/venue")
public class UserVenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping("/list")
    public Result<List<Venue>> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Double lng,
                                    @RequestParam(required = false) Double lat) {
        return Result.success(venueService.listForUser(keyword, lng, lat));
    }

    @GetMapping("/{vid}")
    public Result<Venue> detail(@PathVariable Integer vid) {
        return Result.success(venueService.getDetail(vid));
    }
}