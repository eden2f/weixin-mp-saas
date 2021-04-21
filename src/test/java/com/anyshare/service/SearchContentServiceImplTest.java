package com.anyshare.service;

import cn.hutool.core.util.RandomUtil;
import com.anyshare.enums.AppTag;
import com.anyshare.jpa.es.po.SearchContentPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangminpeng
 * @date 2021/4/7 12:19
 */
@Slf4j
@SpringBootTest
class SearchContentServiceImplTest {

    @Resource
    private SearchContentService searchContentService;

    @Test
    void save() {
        List<SearchContentPO> list = new ArrayList<>();
        String titlesString = "帆乘,楷栋,锋枫,海勇,康帆,安礼,晓平,良帆,瑞翱,涛锟,恒勇,鸿驰,帆强,桓柏,锋寅,博槐,骞琛,桓钊,杰桓,裕枫,福晖,槐仕,奇鹏,骏伟,允潍,乘初,杞郁,柏安,皓宇,骏侠,礼德,哲晓,伟权,祥恒,澄震,浩浩,瑞权,延升,翱楷,锋轩,驰鹏,杞翱,康鹤,材福,晖锐,信恒,凯锦,诚翱,震福,龙宇,祥帆,梓瑞,林龙,日延,槐翰,日寅,起鸿,杞允,瑞锐,仕星,权铭,吉楷,寅星,林帝,皓仕,卓礼,家盛,海寅,盛胤,年禧,畅安,炳龙,柏晖,诚年,彬坤,礼诚,濡凯,驰晨,恒邦,帆晖,诚华,晖星,恒梓,禄彬,鹤华,锟升,桓浩,振尧,祥寅,楷辰,暄帝,锐梓,恒佑,文安,杰畅,加琛,俊泽,乘驰,禄家,中鹤,家锦,皓初,凯震,文韦,彬澄,起哲,海炳,鹤锋,贤逸,哲佑,海信,逸俊,炳年,礼郁,濡寅,晓泽,然延,喆加,天梓,泽锟,轩谛,盛翱,晨子,诚吉,梁鹤,仕逸,升平,奇琛,杞沛,胤邦,辰佑,骞佳,鸿鹏,翱坤,钊峰,哲锐,腾鑫,海阳,烁奇,安芃,浩瑞,星尧,驰炳,安沛,权成,华文,杞晨,柏柔,权彬,祯晨,谛潍,驰安,安日,谷沛,帆华,林翰,然震,琛星,泽杞,澄涛,龙欣,嘉辰,海禄,诚家,帆韦,澄濡,潍延,郁邦,锐礼,蔓材,畅震,腾诚,峰贤,轩潍,凡信,翱年,祜帆,睿吉,祯博,强延,震鑫,邦郁,禧祯,良梁,烁谛,成震,翱颜,加升,荣俊,晨骞,锦槐,烁安,鑫平,沛凯,德升,炳宇,远侠,晖鹏,腾谷,初帆,林然,中禄,斌颜,颜浩,远帆,胤然,祜沛,允锟,畅梁,栋材,泽柔,远锐,杞梁,凯濡,郁槐,家泽,暄乘,年权,文柏,潍禧,澄禧,奇锦,逸然,翰弘,华海,柏辞,瑞星,胤佑,芃嘉,祜胤,逸杰,杰逸,材龙,允升,加韦,逸福,桓辞,枫濡,信然,栋祯,蓓菡,娜嘉,碧珊,菲昕,芸帆,怡莉,鸿莲,曦静,灵玥,橘婧,家曼,鹤丽,岚琳,格梅,呈梦,璇嘉,月欣,楠敏,瑶丽,茹莲,杉歆,帆珍,琬彬,蓓昭,函璇,凌欢,歆蔚,妮萱,琛灵,婷媛,琳凌,岚冬,静鸿,珍倩,桂枫,玉岚,紫涵,桃婧,芳璟,韵珠,香萱,冰梅,桂寒,呈曼,梅茹,雯帆,芙明,紫桂,霞玲,蓓莉,霞可,琪香,菲春,美俊,梦栀,鹤婧,帛菲,静芝,琳沛,玥薇,璟漫,弦茹,彦茜,花梓,歆曦,寒洁,莉梓,颖芙,梓露,玉诗,桃橘,鹤霞,沛漫,月婧,玲阳,雅欢,珍月,璟紫,歆淑,韵琬,月玉,欢彦,寒娅,妍彤,梦萱,蕾芳,心彤,彩妮,寒玲,淑漫,茜桐,春楠,洲茜,月嘉,欣云,珠香,岚韵,娜呈,妮弦,芸函,芳寒,萱林,云琳,莲菡,碧娅,莉凌,栀茜,蔚妮,惠婧,香心,初莉,颖淑,凡芝,璇灵,可蕾,静文,彩鹤,芙鑫,花楠,云梦,菲茹,蓓玥,惠娅,凌芳,柔旭,琛楠,雪娜,蕾诗,芸珠,菡桂,蕾文,桃花,彩诗,彬洁,帆馨,雯春,芝韵,春雅,静紫,寒橘,冰淑,韵露,昭莲,诗晨,钰花,彩月,昕欢,香可,菲婧,橘静,钰韵,霞心,彬雅,芙春,菲惠,萱曦,惠枫,舒薇,楠丽,栀珍,琳惠,歆冬,漫月,莲琛,琬梓,雨美,梦家,倩慧,俊薇,彬璇,敏玉,栀曼,曦倩,蓓蔚,月茜,俊珍,茹帆,璐欢,芝珊,昕舒,娅璐,慧玉,春玥,舒静,颖楠,淑紫,漫彩,欢曼,芳歆,桂冰,芙薇,旭雅,璇冰,岚欣,岚玲,慧锦,克霞,萱丽,采碧,洁昭,采彩,珊云,漫梦,萱凌,怡文,霞杉,敏怡,莉怡,怡露,格漫,瑶薇,沛菲,彦梦,妮馨,菲萱,敏桐,鹤菡,彬蓓,凌彤,珊莉,漫依,琪莲,欣霞,璟心,梅薇,寒蓓,倩梓,采娜,琬雯,枫梅,花格,馨可,杉璐,舒娅,沛香,颖茹,格玲,花柏,雨璟,欣薇";
        String[] titles = titlesString.split(",");
        String[] contents = {"大家好，我是雨天，因为刚好出生在雨天，老爸又刚好姓夏，于是“下雨天”就首发出场了，而且妈妈也希望我如雨后春笋、天天向上哦！呵呵后来因为一部分人实在不忍心在晴朗的天气里叫我雨天，为避免引起“混乱”，就只好舍弃了这个颇有个性的简单名字，退而成“小名“了。我的大名也简单，其实就一个“翌”字，因为我出生的日子刚好是爸爸妈妈结婚2周年纪念日的第二日。“夏翌”谐音宁波话“暇意”，大家希望我暇暇意意，呵呵，有点嫌”翌”字太文绉绉，就把它给拆了，一分为二成羽立了。"
                , "大家都说我是个阳光女孩，因为我是开心果啊。我老是坐不住，呵呵，能跟小椅子成为好朋友，是老师和爸爸妈妈现在的最大心愿。我喜欢跳舞，但最好能跟着音乐自己起舞，不用按老师的要求练基本功；我喜欢画画，但最好是信手涂鸦，把小朋友的脸画成绿色也没关系；我喜欢溜冰，但最有趣的还是约上三五好友，一起练习如何摔跤……"
                , "昀昀是我在妈妈肚子里时的小名哦，我的大名叫曹铁瀛，妈妈怀我的时候和单位里的阿姨们玩牌经常是百战百胜，阿姨们说是因为我的缘故，干脆就叫“天赢”好了，爸妈取其谐音，就变成了“铁瀛”"
                , "嘿！我——21号来报到了！智诰、诰诰、阿诰，你们爱怎么叫就怎么叫吧！反正都是我！我的名字是奶奶请一名先生取的，虽然有点迷信，但寄托了全家人的祝福！"
                , "还是汽车发烧友，小轿车、大卡车、集装箱、大客车、翻斗车、压路机……应有尽有（就差拖拉机还没有报到）。我常在家练习倒车、移库、爬坡、过单轨桥，考取驾照不成问题！！当然我也模拟制造几起车祸，掉进大河，深沟，追尾事故，因为那时我又能大显身手，汽车的零件装了又拆，拆了又装……哈哈！！角角落落都是我的杰作，缺胳膊少腿的，七零八落的，尽管汽车多，可完完整整的没几辆！这下，又有理由去买车啦！"
                , "我还喜欢画画。从小我就拿着笔画一个个的大圆圈，每天乐此不疲，整整画了半年多圆圈呢！够有毅力的吧！老师说我构图线条流畅，就是那样练出来的哦！以后我一定会继续努力的！可是我不太喜欢看书，妈妈让我看书，我老是心不在焉，左顾右盼，常常惹她不高兴，可我就是不喜欢嘛！"
                , "我的性格有点内向、腼腆、不喜欢“显山露水”。我最喜欢体育项目了，最“痴迷”的就是体育频道：乒乓球、羽毛球、台球、足球、篮球、跳水~~，当然我也喜欢打牌（这可是遗传）。"
                , "我的个子很高，被称为“帅哥”，因此彼得女孩子的欢心哦，等我长大了，我要娶七个“老婆”，其中三个为：外婆、爸爸和妈妈。 我的目标是考上北大。\n"
                , "我当时的第一反应是：这两个字可以跟名人的名字做一个联系，然后做一个介绍。我说你介绍的时候可以跟别人讲："
        };
        // 模拟数据
        for (int i = 0; i < titles.length; i++) {
            int i1 = RandomUtil.randomInt(titles.length);
            int i2 = RandomUtil.randomInt(contents.length);
            // 注意这里,第一个参数是id,所以无论你运行多少次,都只会添加100条数据,如果id存在es更新,不存在添加
            SearchContentPO searchContent = SearchContentPO.create();
            searchContent.setId((long) i);
            searchContent.setTitle(titles[i1]);
            searchContent.setDigest(titles[i % titles.length]);
            searchContent.setContent(contents[i2]);
            searchContent.setAppTag(AppTag.Test.getCode());
            list.add(searchContent);
        }
        searchContentService.save(list);
    }

    @Test
    void testSave() {
    }

    @Test
    void findByTitle() {
        List<SearchContentPO> searchContents = searchContentService.findByTitle("帆乘");
        Assert.isTrue(CollectionUtils.isNotEmpty(searchContents), "findByTitle");
        log.info("searchContents = {}", searchContents);
    }

    @Test
    void findByTitleOrDigestOrContent() {
        List<SearchContentPO> searchContentPage = searchContentService.findByTitleOrDigestOrContent(AppTag.Test, "我");
        Assert.isTrue(CollectionUtils.isNotEmpty(searchContentPage), "findByTitleOrDigestOrContent");
        log.info("searchContents = {}", searchContentPage);
    }
}