/**
 * PTStemmer - Java Stemming toolkit for the Portuguese language (C) 2008 Pedro Oliveira
 * 
 * This file is part of PTStemmer.
 * PTStemmer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PTStemmer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with PTStemmer. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.itera.preprocess.stempt;

import java.io.Serializable;


/**
 * Abstract class that provides the main features to all the stemmers
 * @author Pedro Oliveira
 *
 */
public abstract class Stemmer implements Serializable {

	private boolean cacheStems;
	private LRUCache lruCache;

        private boolean ignoreStopWords;

	public enum StemmerType{ORENGO, PORTER};

	/**
	 * Factory to stemmer construction
	 * @param stype
	 * @return
	 */
	public static Stemmer StemmerFactory(StemmerType stype)
	{
		if(stype == StemmerType.ORENGO)
			return new OrengoStemmer();
		else
			return new PorterStemmer();
	}

	/**
	 * Create a LRU Cache, caching the last <code>size</code> stems
	 * @param size
	 */
	public void enableCaching(int size)
	{
		cacheStems = true;
		lruCache = new LRUCache(size);
	}

	/**
	 * Disable and deletes the LRU Cache
	 */
	public void disableCaching()
	{
		cacheStems = false;
		lruCache = null;
	}

	/**
	 * Check if LRU Cache is enabled
	 * @return
	 */
	public boolean isCachingEnabled()
	{
		return cacheStems;
	}


	/**
	 * Performs stemming on the <code>phrase</code>, using a simple space tokenizer
	 * @param phrase
	 * @return
	 */
	public String[] phraseStemming(String phrase)
	{
		String[] res = phrase.split(" ");
		for(int i=0; i<res.length; i++)
			res[i] = wordStemming(res[i]);
		return res;
	}

	/**
	 * Performs stemming on the <code>word</code>
	 * @param word
	 * @return
	 */
	public String wordStemming(String word)
	{
		String res;
		if(word.contains(" "))
			return word;
		else
		{
			word = word.trim().toLowerCase();
			
			if(cacheStems)
				if(lruCache.containsKey(word))
					return lruCache.get(word);

			res = stemming(word);

			if(cacheStems)
				lruCache.put(word, res);
			return res;
		}
	}
        
        public String getWordStem(String word) {
            return wordStemming(word);
        }

	protected abstract String stemming(String word);
}
