package com.simplelife.renhai.server.db;

import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Impresscard entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.ImpressCard
  * @author MyEclipse Persistence Tools 
 */
public class ImpressCardDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(ImpressCardDAO.class);
		//property constants
	public static final String CHAT_TOTAL_COUNT = "chatTotalCount";
	public static final String CHAT_TOTAL_DURATION = "chatTotalDuration";
	public static final String CHAT_LOSS_COUNT = "chatLossCount";



    
    public void save(ImpressCard transientInstance) {
        log.debug("saving Impresscard instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(ImpressCard persistentInstance) {
        log.debug("deleting Impresscard instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public ImpressCard findById( java.lang.Integer id) {
        log.debug("getting Impresscard instance with id: " + id);
        try {
            ImpressCard instance = (ImpressCard) getSession()
                    .get("com.simplelife.renhai.server.db.Impresscard", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<ImpressCard> findByExample(ImpressCard instance) {
        log.debug("finding Impresscard instance by example");
        try {
            List<ImpressCard> results = (List<ImpressCard>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Impresscard")
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
      log.debug("finding Impresscard instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Impresscard as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<ImpressCard> findByChatTotalCount(Object chatTotalCount
	) {
		return findByProperty(CHAT_TOTAL_COUNT, chatTotalCount
		);
	}
	
	public List<ImpressCard> findByChatTotalDuration(Object chatTotalDuration
	) {
		return findByProperty(CHAT_TOTAL_DURATION, chatTotalDuration
		);
	}
	
	public List<ImpressCard> findByChatLossCount(Object chatLossCount
	) {
		return findByProperty(CHAT_LOSS_COUNT, chatLossCount
		);
	}
	

	public List findAll() {
		log.debug("finding all Impresscard instances");
		try {
			String queryString = "from Impresscard";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public ImpressCard merge(ImpressCard detachedInstance) {
        log.debug("merging Impresscard instance");
        try {
            ImpressCard result = (ImpressCard) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(ImpressCard instance) {
        log.debug("attaching dirty Impresscard instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(ImpressCard instance) {
        log.debug("attaching clean Impresscard instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}