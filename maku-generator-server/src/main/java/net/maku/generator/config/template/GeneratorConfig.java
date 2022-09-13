package net.maku.generator.config.template;

import cn.hutool.core.util.StrUtil;
import net.maku.generator.common.exception.ServerException;
import net.maku.generator.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 代码生成配置内容
 *
 * @author 阿沐 babamu@126.com
 */
@Configuration
public class GeneratorConfig {
    @Value("${generator.template}")
    private String template;

    public GeneratorInfo getGeneratorConfig() {
        if (StrUtil.isBlank(template)) {
            throw new ServerException("模板不存在，需指定模板");
        }

        // 模板所在路径
        String templatePath = "/template/" + template + "/";

        // 模板配置文件
        InputStream isConfig = this.getClass().getResourceAsStream(templatePath + "config.json");
        if (isConfig == null) {
            throw new ServerException("模板配置文件，config.json不存在");
        }

        try {
            // 读取模板配置文件
            String configContent = StreamUtils.copyToString(isConfig, StandardCharsets.UTF_8);
            GeneratorInfo generator = JsonUtils.parseObject(configContent, GeneratorInfo.class);
            for (TemplateInfo templateInfo : generator.getTemplates()) {
                // 模板文件
                InputStream isTemplate = this.getClass().getResourceAsStream(templatePath + templateInfo.getTemplateName());
                if (isTemplate == null) {
                    throw new ServerException("模板文件 " + templateInfo.getTemplateName() + " 不存在");
                }
                // 读取模板内容
                String templateContent = StreamUtils.copyToString(isTemplate, StandardCharsets.UTF_8);

                templateInfo.setTemplateContent(templateContent);
            }
            return generator;
        } catch (IOException e) {
            throw new ServerException("读取config.json配置文件失败");
        }
    }
}
