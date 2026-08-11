package com.example.badminton_team.controller.merchant;

import com.example.badminton_team.dto.Result;
import com.example.badminton_team.dto.VenueDto;
import com.example.badminton_team.entity.Merchant;
import com.example.badminton_team.entity.Venue;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.MerchantService;
import com.example.badminton_team.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/merchant/venue")
public class MerchantVenueController {

    @Autowired
    private VenueService venueService;

    @Autowired
    private MerchantService merchantService;   // 新增

    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody VenueDto dto,
                         @RequestAttribute("mid") Integer mid) {
        merchantService.checkMerchantStatus(mid);     // 权限检查
        venueService.add(dto, mid);
        return Result.success("添加成功", null);
    }

    @PutMapping("/update/{vid}")
    public Result<?> update(@PathVariable Integer vid,
                            @Valid @RequestBody VenueDto dto,
                            @RequestAttribute("mid") Integer mid) {
        merchantService.checkMerchantStatus(mid);     // 权限检查
        dto.setVid(vid);
        venueService.update(dto, mid);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{vid}")
    public Result<?> delete(@PathVariable Integer vid,
                            @RequestAttribute("mid") Integer mid) {
        merchantService.checkMerchantStatus(mid);     // 权限检查
        venueService.delete(vid, mid);
        return Result.success("删除成功", null);
    }

    @GetMapping("/my")
    public Result<List<Venue>> myVenues(@RequestAttribute("mid") Integer mid) {
        //merchantService.checkMerchantStatus(mid);     // 权限检查
        return Result.success(venueService.getByMerchant(mid));
    }
}
