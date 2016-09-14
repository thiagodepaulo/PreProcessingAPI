/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier;

import com.itera.structures.Data;
import com.itera.structures.IndexValue;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classifier interface. All schemes for numeric or nominal prediction in
 * Weka implement this interface. Note that a classifier MUST either implement
 * distributionForInstance() or classifyInstance().
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision$
 */
public interface Classifier extends Serializable {

  /** 
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @exception Exception if the classifier has not been
   * generated successfully
   */
  public abstract void buildClassifier(Data data) throws Exception;

  /**
   * Classifies the given test instance. The instance has to belong to a
   * dataset when it's being classified. Note that a classifier MUST
   * implement either this or distributionForInstance().
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   * Utils.missingValue() if no prediction is made
   * @exception Exception if an error occurred during the prediction
   */
  public int classifyInstance(ArrayList<IndexValue> instance) throws Exception;

  /**
   * Predicts the class memberships for a given instance. If
   * an instance is unclassified, the returned array elements
   * must be all zero. If the class is numeric, the array
   * must consist of only one element, which contains the
   * predicted value. Note that a classifier MUST implement
   * either this or classifyInstance().
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership
   * probabilities of the test instance in each class
   * or the numeric prediction
   * @exception Exception if distribution could not be
   * computed successfully
   */
  public double[] distributionForInstance(ArrayList<IndexValue> instance) throws Exception;

}