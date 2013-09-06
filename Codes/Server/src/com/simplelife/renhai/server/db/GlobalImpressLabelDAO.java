package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Globalimpresslabel entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.GlobalImpressLabel
  * @author MyEclipse Persistence Tools 
 */
public class GlobalImpressLabelDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(GlobalImpressLabelDAO.class);
		//property constants
	public static final String IMPRESS_LABEL = "impressLabel";
	public static final String GLOBAL_ASSESS_COUNT = "globalAssessCount";



    
    public void save(GlobalImpressLabel transientInstance) {
        log.debug("saving Globalimpresslabel instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(GlobalImpressLabel persistentInstance) {
        log.debug("deleting Globalimpresslabel instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public GlobalImpressLabel findById( java.lang.Integer id) {
        log.debug("getting Globalimpresslabel instance with id: " + id);
        try {
            GlobalImpressLabel instance = (GlobalImpressLabel) getSession()
                    .get("com.simplelife.renhai.server.db.Globalimpresslabel", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<GlobalImpressLabel> findByExample(GlobalImpressLabel instance) {
        log.debug("finding Globalimpresslabel instance by example");
        try {
            List<GlobalImpressLabel> results = (List<GlobalImpressLabel>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Globalimpresslabel")
                    .add( create(instance) )
            .list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }    
    
    public List findByProperty(String propertyName, Object value) {
      log.debug("finding Globalimpresslabel instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Globalimpresslabel as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<GlobalImpressLabel> findByImpressLabel(Object impressLabel
	) {
		return findByProperty(IMPRESS_LABEL, impressLabel
		);
	}
	
	public List<GlobalImpressLabel> findByGlobalAssessCount(Object globalAssessCount
	) {
		return findByProperty(GLOBAL_ASSESS_COUNT, globalAssessCount
		);
	}
	

	public List findAll() {
		log.debug("finding all Globalimpresslabel instances");
		try {
			String queryString = "from Globalimpresslabel";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public GlobalImpressLabel merge(GlobalImpressLabel detachedInstance) {
        log.debug("merging Globalimpresslabel instance");
        try {
            GlobalImpressLabel result = (GlobalImpressLabel) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(GlobalImpressLabel instance) {
        log.debug("attaching dirty Globalimpresslabel instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(GlobalImpressLabel instance) {
        log.debug("attaching clean Globalimpresslabel instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}