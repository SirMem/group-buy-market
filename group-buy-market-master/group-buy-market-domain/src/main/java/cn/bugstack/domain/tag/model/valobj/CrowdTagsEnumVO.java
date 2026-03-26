package cn.bugstack.domain.tag.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CrowdTagsEnumVO {



    ORIGIN("原始默认标签", "sdfasdf1212"),
    POTENTIAL("潜在用户标签","RQ_KJHKL98UU78H66554GFDV")
    ;





    private String TagName;
    private String TagId;

}
