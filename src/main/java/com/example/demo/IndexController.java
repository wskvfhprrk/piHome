package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@Slf4j
public class IndexController {

    @Autowired
    private IoService ioService;
    @Autowired
    private SerialUtils serialUtils;

    @GetMapping("home")
    public String home(Model model, HomeVo vo) {
        /**
         * 1、如果没有带参数，表示第一个组类型
         * 2、如果带了参数groupName,没有带swithcName,表示没有选择房间
         * 3、如果带了所有参数，表示点击事件。
         */
        //
        List<Map> all = ioService.getAll();
        List<String> groupName = all.stream().map(map -> map.get("groupName").toString()).distinct().collect(Collectors.toList());
        String title;
        //没有给标题的时候
        if (vo == null || vo.getGroupName() == null || vo.getGroupName().length() == 0) {
            title = groupName.get(0);
        } else {
            title = vo.getGroupName();
        }
        model.addAttribute("title", title);
        model.addAttribute("groupName", groupName);
        List<Map> list = all.stream().filter(map -> map.get("groupName").toString().equals(title)).collect(Collectors.toList());
        model.addAttribute("list", list);
        //如果开关名称和状态都有表示点击事件
        return "home";
    }

    @RequestMapping("filpSwitch")
    @ResponseBody
    public String filpSwitch(HomeVo vo) throws IOException {
        List<Map> all = ioService.getAll();
        List<Map> list = all.stream().filter(map -> map.get("groupName").toString().equals(vo.getGroupName()) && map.get("switchName").toString().equals(vo.getSwitchName())).collect(Collectors.toList());
        if (list.isEmpty()) {
            return "没有此按纽命令！";
        } else {
            Map map = list.get(0);
            if (vo.isStatus()) {
                log.info("开信号");
                serialUtils.sendHex(map.get("openOutHex").toString());
                ioService.updateStastus(map, true);
            } else {
                log.info("关信号");
                serialUtils.sendHex(map.get("closeOutHex").toString());
                ioService.updateStastus(map, true);
            }
            return "发送命令成功！";
        }
    }

    public static void main(String[] args) throws Exception {
        //
        Stream<String> lines = Files.lines(Paths.get("D:\\1.csv"));
        lines.forEach(s -> System.out.println(s));
    }
}