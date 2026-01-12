package com.example.luofushan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.dto.req.ExchangeItemReq;
import com.example.luofushan.dto.req.MallItemListReq;
import com.example.luofushan.dto.req.MyExchangeListReq;
import com.example.luofushan.dto.resp.ExchangeResultResp;
import com.example.luofushan.dto.resp.MallItemListResp;
import com.example.luofushan.dto.resp.MyExchangeRecordResp;

public interface ExchangeService {

    Page<MyExchangeRecordResp> getMyExchangeList(MyExchangeListReq req);

    Page<MallItemListResp> getMallItemList(MallItemListReq req);

    ExchangeResultResp exchangeItem(ExchangeItemReq req);
}
