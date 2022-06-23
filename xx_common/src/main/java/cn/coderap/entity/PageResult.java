package cn.coderap.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResult<T> {
    private Long total;//总记录数
    private List<T> rows;//一页记录
}
