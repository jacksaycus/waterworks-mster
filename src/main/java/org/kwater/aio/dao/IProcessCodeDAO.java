package org.kwater.aio.dao;

import org.kwater.aio.dto.ProcessCodeDTO;

import java.util.List;

public interface IProcessCodeDAO
{
    List<ProcessCodeDTO> select();
}
