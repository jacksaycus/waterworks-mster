package org.kwater.aio.util;

import org.kwater.aio.dto.TagDescriptionDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@ToString
public class TagDescriptionList
{
    private List<TagDescriptionDTO> tagDescriptionList;

    @PostConstruct
    public void init()
    {
        tagDescriptionList = new ArrayList<>();
    }

    public int getSize()
    {
        int nSize = tagDescriptionList.size();
        return nSize;
    }

    public TagDescriptionDTO getTagDescriptionFromName(String name)
    {
//        tagDescriptionList
//                .stream()
//                .filter(list -> list.getName().equalsIgnoreCase(name))
//                .findAny();
        for(TagDescriptionDTO dto : tagDescriptionList)
        {
            if(dto.getName().equalsIgnoreCase(name) == true)
            {
                return dto;
            }
        }
        return null;
    }

    public void addTagDescription(TagDescriptionDTO dto)
    {
        tagDescriptionList.add(dto);
    }
}
