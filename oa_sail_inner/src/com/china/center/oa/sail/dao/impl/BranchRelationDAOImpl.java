package com.china.center.oa.sail.dao.impl;

import com.china.center.jdbc.inter.impl.BaseDAO;
import com.china.center.jdbc.util.ConditionParse;
import com.china.center.oa.sail.bean.BranchRelationBean;
import com.china.center.oa.sail.dao.BranchRelationDAO;
import com.china.center.oa.sail.vo.BranchRelationVO;
import java.util.List;

public class BranchRelationDAOImpl extends BaseDAO<BranchRelationBean, BranchRelationVO> implements BranchRelationDAO
{

	public List<BranchRelationVO> queryVOsByCondition(ConditionParse con)
	{
		return this.queryEntityVOsByCondition(con);
	}
}
