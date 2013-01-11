/*

 Copyright or © or Copr. Ecole des Mines d'Alès (2012) 

 This software is a computer program whose purpose is to 
 process semantic graphs.

 This software is governed by the CeCILL  license under French law and
 abiding by the rules of distribution of free software.  You can  use, 
 modify and/ or redistribute the software under the terms of the CeCILL
 license as circulated by CEA, CNRS and INRIA at the following URL
 "http://www.cecill.info". 

 As a counterpart to the access to the source code and  rights to copy,
 modify and redistribute granted by the license, users are provided only
 with a limited warranty  and the software's author,  the holder of the
 economic rights,  and the successive licensors  have only  limited
 liability. 

 In this respect, the user's attention is drawn to the risks associated
 with loading,  using,  modifying and/or developing or reproducing the
 software by the user in light of its specific status of free software,
 that may mean  that it is complicated to manipulate,  and  that  also
 therefore means  that it is reserved for developers  and  experienced
 professionals having in-depth computer knowledge. Users are therefore
 encouraged to load and test the software's suitability as regards their
 requirements in conditions enabling the security of their systems and/or 
 data to be ensured and,  more generally, to use and operate it in the 
 same conditions as regards security. 

 The fact that you are presently reading this means that you have had
 knowledge of the CeCILL license and that you accept its terms.

 */
package slib.sml.sm.core.metrics.ic.topo;

import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slib.sglib.model.graph.elements.V;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.utils.LogBasedMetric;
import slib.sml.sm.core.utils.MathSML;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.impl.ResultStack;

/**
 *
 * Reference: Sanchez D, Batet M, Isern D: Ontology-based information content computation.
 * Knowledge-Based Systems 2011, 24:297-303.
 *
 * formula equation 10 p 300
 *
 * IC inner expression range : ]0,1] IC value : [0,...[
 *
 *
 * @author Harispe Sébastien
 */
public class ICi_sanchez_2011_a extends LogBasedMetric implements ICtopo {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *
     * @param allNbOfReachableLeaves
     * @param allNbAncestors
     * @return
     * @throws SLIB_Ex_Critic
     */
    public ResultStack<V, Double> compute(ResultStack<V, Double> allNbOfReachableLeaves,
            ResultStack<V, Double> allNbAncestors) throws SLIB_Ex_Critic {

        ResultStack<V, Double> results = new ResultStack<V, Double>(this.getClass().getSimpleName());

        double max_leaves = allNbOfReachableLeaves.getMax();

        double nbLeavesExclusif, nbAncestorsInc, cur_ic;

        for (V v : allNbAncestors.keySet()) {

            nbAncestorsInc = allNbAncestors.get(v).doubleValue();
            nbLeavesExclusif = allNbOfReachableLeaves.get(v).doubleValue();

            cur_ic = compute(nbLeavesExclusif, nbAncestorsInc, max_leaves);

            //logger.info(v+" --> "+cur_ic);


            results.add(v, cur_ic);
        }

        return results;
    }

    /**
     * Private due to log base
     *
     * @param nbLeaves
     * @param nbAncestors
     * @param maxLeaves
     * @return
     */
    private double compute(double nbLeaves, double nbAncestors, double maxLeaves) {

        //logger.info("NB leaves "+nbLeaves);
        double x = nbLeaves / nbAncestors + 1.;

        return -MathSML.log(x / (maxLeaves + 1.), getLogBase());
    }

    @Override
    public ResultStack<V, Double> compute(IC_Conf_Topo conf, SM_Engine manager)
            throws SLIB_Ex_Critic {


        setLogBase(conf);


        ResultStack<V, Double> allNbAncestors = manager.getAllNbAncestorsInc();
        ResultStack<V, Double> allNbReachableLeaves = manager.getAllNbReachableLeaves();

        // getAllNbReachableLeaves() is inclusive and Sanchez measure require excluvive i.e.
        // if a concept is a leaf it must not be contained in the set of reachable leaves

        Set<V> leaves = manager.getLeaves();

        ResultStack<V, Double> correctedNbReachableLeaves = new ResultStack<V, Double>();
        for (Map.Entry<V, Double> e : allNbReachableLeaves.entrySet()) {
            correctedNbReachableLeaves.add(e.getKey(), e.getValue());
        }
        for (V v : leaves) {
            double corrected = correctedNbReachableLeaves.get(v) - 1;
            correctedNbReachableLeaves.add(v, corrected);
        }

        return compute(correctedNbReachableLeaves, allNbAncestors);
    }
}
