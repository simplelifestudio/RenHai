package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Hotinterestlabelstatistics entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.HotInterestLabelStatistics
  * @author MyEclipse Persistence Tools 
 */
public class HotInterestLabelStatisticsDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(HotInterestLabelStatisticsDAO.class);
		//property constants
	public static final String SAVE_TIME = "saveTime";
	public static final String COUNT = "count";



    
    public void save(HotInterestLabelStatistics transientInstance) {
        log.debug("saving Hotinterestlabelstatistics instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(HotInterestLabelStatistics persistentInstance) {
        log.debug("deleting Hotinterestlabelstatistics instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public HotInterestLabelStatistics findById( java.lang.Integer id) {
        log.debug("getting Hotinterestlabelstatistics instance with id: " + id);
        try {
            HotInterestLabelStatistics instance = (HotInterestLabelStatistics) getSession()
                    .get("com.simplelife.renhai.server.db.Hotinterestlabelstatistics", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<HotInterestLabelStatistics> findByExample(HotInterestLabelStatistics instance) {
        log.debug("finding Hotinterestlabelstatistics instance by example");
        try {
            List<HotInterestLabelStatistics> results = (List<HotInterestLabelStatistics>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Hotinterestlabelstatistics")
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
      log.debug("finding Hotinterestlabelstatistics instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Hotinterestlabelstatistics as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<HotInterestLabelStatistics> findBySaveTime(Object saveTime
	) {
		return findByProperty(SAVE_TIME, saveTime
		);
	}
	
	public List<HotInterestLabelStatistics> findByCount(Object count
	) {
		return findByProperty(COUNT, count
		);
	}
	

	public List findAll() {
		log.debug("finding all Hotinterestlabelstatistics instances");
		try {
			String queryString = "from Hotinterestlabelstatistics";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public HotInterestLabelStatistics merge(HotInterestLabelStatistics detachedInstance) {
        log.debug("merging Hotinterestlabelstatistics instance");
        try {
            HotInterestLabelStatistics result = (HotInterestLabelStatistics) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(HotInterestLabelStatistics instance) {
        log.debug("attaching dirty Hotinterestlabelstatistics instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(HotInterestLabelStatistics instance) {
        log.debug("attaching clean Hotinterestlabelstatistics instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}