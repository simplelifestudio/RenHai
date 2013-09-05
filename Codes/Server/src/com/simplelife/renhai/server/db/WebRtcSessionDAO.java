package com.simplelife.renhai.server.db;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Webrtcsession entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.Webrtcsession
  * @author MyEclipse Persistence Tools 
 */
public class WebrtcsessionDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(WebrtcsessionDAO.class);
		//property constants
	public static final String WEBRTCSESSION = "webrtcsession";
	public static final String REQUEST_DATE = "requestDate";
	public static final String TOKEN = "token";
	public static final String TOKEN_UPDATE_DATE = "tokenUpdateDate";
	public static final String EXPIRATION_DATE = "expirationDate";



    
    public void save(Webrtcsession transientInstance) {
        log.debug("saving Webrtcsession instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(Webrtcsession persistentInstance) {
        log.debug("deleting Webrtcsession instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public Webrtcsession findById( java.lang.Integer id) {
        log.debug("getting Webrtcsession instance with id: " + id);
        try {
            Webrtcsession instance = (Webrtcsession) getSession()
                    .get("com.simplelife.renhai.server.db.Webrtcsession", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<Webrtcsession> findByExample(Webrtcsession instance) {
        log.debug("finding Webrtcsession instance by example");
        try {
            List<Webrtcsession> results = (List<Webrtcsession>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Webrtcsession")
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
      log.debug("finding Webrtcsession instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Webrtcsession as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<Webrtcsession> findByWebrtcsession(Object webrtcsession
	) {
		return findByProperty(WEBRTCSESSION, webrtcsession
		);
	}
	
	public List<Webrtcsession> findByRequestDate(Object requestDate
	) {
		return findByProperty(REQUEST_DATE, requestDate
		);
	}
	
	public List<Webrtcsession> findByToken(Object token
	) {
		return findByProperty(TOKEN, token
		);
	}
	
	public List<Webrtcsession> findByTokenUpdateDate(Object tokenUpdateDate
	) {
		return findByProperty(TOKEN_UPDATE_DATE, tokenUpdateDate
		);
	}
	
	public List<Webrtcsession> findByExpirationDate(Object expirationDate
	) {
		return findByProperty(EXPIRATION_DATE, expirationDate
		);
	}
	

	public List findAll() {
		log.debug("finding all Webrtcsession instances");
		try {
			String queryString = "from Webrtcsession";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public Webrtcsession merge(Webrtcsession detachedInstance) {
        log.debug("merging Webrtcsession instance");
        try {
            Webrtcsession result = (Webrtcsession) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(Webrtcsession instance) {
        log.debug("attaching dirty Webrtcsession instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Webrtcsession instance) {
        log.debug("attaching clean Webrtcsession instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}