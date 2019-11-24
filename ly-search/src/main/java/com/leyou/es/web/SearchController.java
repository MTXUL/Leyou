package com.leyou.es.web;

import com.leyou.common.vo.PageResult;
import com.leyou.es.pojo.Goods;
import com.leyou.es.pojo.SearchRequest;
import com.leyou.es.pojo.SearchResult;
import com.leyou.es.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;
    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(searchService.search(searchRequest));
    }

}
