package com.simplelife.renhai.server.db;

import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 	* A data access object (DAO) providing persistence and search support for Moduledefinition entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.simplelife.renhai.server.db.ModuleDefinition
  * @author MyEclipse Persistence Tools 
 */
public class ModuleDefinitionDAO extends BaseHibernateDAO  {
	     private static final Logger log = LoggerFactory.getLogger(ModuleDefinitionDAO.class);
		//property constants
	public static final String MODULE_ID = "moduleId";
	public static final String DESCRIPTION = "description";



    
    public void save(ModuleDefinition transientInstance) {
        log.debug("saving Moduledefinition instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(ModuleDefinition persistentInstance) {
        log.debug("deleting Moduledefinition instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public ModuleDefinition findById( java.lang.Integer id) {
        log.debug("getting Moduledefinition instance with id: " + id);
        try {
            ModuleDefinition instance = (ModuleDefinition) getSession()
                    .get("com.simplelife.renhai.server.db.Moduledefinition", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<ModuleDefinition> findByExample(ModuleDefinition instance) {
        log.debug("finding Moduledefinition instance by example");
        try {
            List<ModuleDefinition> results = (List<ModuleDefinition>) getSession()
                    .createCriteria("com.simplelife.renhai.server.db.Moduledefinition")
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
      log.debug("finding Moduledefinition instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Moduledefinition as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	public List<ModuleDefinition> findByModuleId(Object moduleId
	) {
		return findByProperty(MODULE_ID, moduleId
		);
	}
	
	public List<ModuleDefinition> findByDescription(Object description
	) {
		return findByProperty(DESCRIPTION, description
		);
	}
	

	public List findAll() {
		log.debug("finding all Moduledefinition instances");
		try {
			String queryString = "from Moduledefinition";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public ModuleDefinition merge(ModuleDefinition detachedInstance) {
        log.debug("merging Moduledefinition instance");
        try {
            ModuleDefinition result = (ModuleDefinition) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(ModuleDefinition instance) {
        log.debug("attaching dirty Moduledefinition instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(ModuleDefinition instance) {
        log.debug("attaching clean Moduledefinition instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}