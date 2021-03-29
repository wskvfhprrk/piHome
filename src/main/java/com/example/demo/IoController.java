package com.example.demo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/3 11:38
 */
@RestController
@RequestMapping("io")
@Api(value = "开关数据维护接口")
public class IoController {

    @Autowired
    private IoService ioService;

    /**
     * 批量添加数据
     *
     * @param data 格式每条以分号隔开，每个值以逗号隔开
     * @return
     */
    @ApiOperation(value = "批量添加数据", notes = "批量添加数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "data", value = "使用csv数据——inHex,分类，开关名称，状态，outHex", required = true, dataType = "string")
    })
    @PostMapping("add")
    public Result add(String data) {
        Result s = ioService.addAll(data);
        return s;
    }

    @PostMapping("csv")
    public Result csv() {
        return Result.ok();
    }


    /**
     * 根据输入Hex删除该数据
     *
     * @param inHex
     * @return
     */
    @ApiOperation(value = "根据输入Hex删除该数据", notes = "根据输入Hex删除该数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "inHex", value = "inHex", required = true, dataType = "string")
    })
    @DeleteMapping("{inHex}")
    public Result delete(@PathVariable String inHex) {
        Result delete = ioService.delete(inHex);
        return delete;
    }

    /**
     * 根据输入的Hex查询信息
     *
     * @param inHex
     * @return
     */
    @ApiOperation(value = "根据输入的Hex查询信息", notes = "根据输入的Hex查询信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "inHex", value = "inHex", required = true, dataType = "string")
    })
    @GetMapping("{inHex}")
    public Result getByInHex(@PathVariable String inHex) {
        Result<IoEntity> mapResult = ioService.selectByInHex(inHex);
        return mapResult;
    }

    /**
     * 查询所有接品数据
     *
     * @return
     */
    @ApiOperation(value = "查询所有接品数据", notes = "查询所有接品数据")
    @GetMapping
    public Result<List<Map>> getAll() {
        Result<List<Map>> all = ioService.get();
        return all;
    }

    /**
     * 根据inHex学习——发射射频信号
     *
     * @return
     */
    @ApiOperation(value = "根据inHex学习——发射射频信号", notes = "根据inHex学习——发射射频信号")
    @GetMapping("study/{inHex}")
    public Result study(@PathVariable String inHex) {
        ioService.commandLine(inHex);
        return Result.ok();
    }


    /**
     * 重置数据——清空之前所有数据
     *
     * @return
     */
    @ApiOperation(value = "重置命令——全部清空", notes = "重置命令——全部清空")
    @GetMapping("reset")
    public Result rest() {
        Result reset = ioService.reset();
        return reset;
    }
}
