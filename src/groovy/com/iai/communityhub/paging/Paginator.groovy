package com.iai.communityhub.paging

import java.util.Collection

import org.springframework.jdbc.core.JdbcTemplate

import com.iai.communityhub.dao.AbstractDao

class Paginator<T> {
	
	def dao
	
	def pageSize	
	def numObjects
	def numPages
	
	def curPage
	
	/**
	 * Constructor 
	 * 
	 * @param dao
	 * @param pageSize
	 */
	Paginator(AbstractDao dao, def pageSize) {
		this.dao = dao; 
		this.pageSize = pageSize
		this.numObjects = dao.count(); 
		if (numObjects > 0)
			// count pages if we have objects 
			this.numPages = Math.ceil(numObjects / pageSize).toInteger();
		else
			// there are always at least one page (may be empty)
			this.numPages = 1; 
		this.curPage = 1; 
	}
	
	/**
	 * Sets the current page 
	 * 
	 * @param page
	 * @throws IllegalArgumentException
	 */
	public void setCurPage(int page) throws IllegalArgumentException {
		if (page <= 0)
			throw new IllegalArgumentException("Not a valid page");
		if (page > numPages)
			throw new IllegalArgumentException("There are not that many pages, max: " + numPages);
		// set the page 
		curPage = page;	
	}
	
	/**
	 * Returns true if the current page is the last page 
	 * 
	 * @return
	 */
	public boolean isLastPage() { 
		return curPage >= numPages;
	}

	/**
	 * Returns true if the current page is the first page 
	 * 
	 * @return
	 */
	public boolean isFirstPage() {
		return curPage <= 1;
	}
	
	/**
	 * Returns the next page number 
	 * 
	 * @return
	 */
	public int nextPage() {
		// returns the current page if we are on the last page 
		if (isLastPage())
			return curPage;
		// otherwise, returns the next page  
		return curPage + 1;
	}
	
	/**
	 * Returns he previous page number 
	 * 
	 * @return
	 */
	public int prevPage() {
		// returns the current page if we are on the first page 
		if (isFirstPage())
			return curPage;
		// otherwise, returns the previous page
		return curPage - 1; 
	}
	
	/**
	 * Returns the number of objects 
	 * 
	 * @return
	 */
	public int count() {
		return numObjects;
	}
	
	/**
	 * Returns true if page selection is needed, false otherwise
	 * 
	 * @return
	 */
	public boolean needed() {
		return count() > pageSize;
	}
	
	/**
	 * Returns the objects in the current page
	 *  
	 * @return
	 */
	public Collection<T> objects() {
		return dao.list(pageSize, (curPage - 1) * pageSize); 
	}
	
}
