package edu.calpoly.csc.scheduler.model.db;

/**
 *
 * @author Eric Liebowitz
 * @version Oct 22, 2011
 */
public abstract class DbData
{
   public abstract void verify () throws NullDataException;
}
