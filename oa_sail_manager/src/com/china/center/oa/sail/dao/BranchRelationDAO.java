package com.china.center.oa.sail.dao;

import com.china.center.jdbc.inter.DAO;
import com.china.center.jdbc.util.ConditionParse;
import com.china.center.jdbc.util.PageSeparate;
import com.china.center.oa.sail.bean.BranchRelationBean;
import com.china.center.oa.sail.vo.BranchRelationVO;

import java.util.List;

public interface BranchRelationDAO extends DAO<BranchRelationBean, BranchRelationVO>
{
	List<BranchRelationVO> queryVOsByCondition(ConditionParse con);

	
}
