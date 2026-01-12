package com.example.luofushan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.req.ExchangeItemReq;
import com.example.luofushan.dto.req.MallItemListReq;
import com.example.luofushan.dto.req.MyExchangeListReq;
import com.example.luofushan.dto.resp.ExchangeResultResp;
import com.example.luofushan.dto.resp.MallItemListResp;
import com.example.luofushan.dto.resp.MyExchangeRecordResp;
import com.example.luofushan.service.ExchangeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExchangeController {

    @Resource
    private ExchangeService exchangeService;

    @GetMapping("/exchange/my")
    public Result<Page<MyExchangeRecordResp>> getMyExchangeList(MyExchangeListReq req) {
        return Result.buildSuccess(exchangeService.getMyExchangeList(req));
    }

    @GetMapping("/mall/item/list")
    public Result<Page<MallItemListResp>> getMallItemList(MallItemListReq req) {
        return Result.buildSuccess(exchangeService.getMallItemList(req));
    }

    @PostMapping("/mall/item/exchange")
    public Result<ExchangeResultResp> exchangeItem(@RequestBody ExchangeItemReq req) {
        return Result.buildSuccess(exchangeService.exchangeItem(req));
    }
}
