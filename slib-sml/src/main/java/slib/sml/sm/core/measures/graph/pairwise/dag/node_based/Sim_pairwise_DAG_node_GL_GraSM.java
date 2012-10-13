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
 
 
package slib.sml.sm.core.measures.graph.pairwise.dag.node_based;

import java.util.Set;

import slib.sglib.model.graph.elements.V;
import slib.sml.sm.core.utils.SM_Engine;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;

public class Sim_pairwise_DAG_node_GL_GraSM implements Sim_DAG_node_abstract{
	
	public static final String beta_param_name = "beta";
	
	private double beta  = 0.;
	
	
	public double sim(V a, V b, SM_Engine c, SMconf conf) throws SLIB_Exception {
		
		double ic_a = c.getIC(conf.getICconf(),a);
		double ic_b = c.getIC(conf.getICconf(),b);
		
		Set<V> disjointAncs = c.getDisjointCommonAncestors(a, b);
		double sumIC = 0.;
		
		for(V v:disjointAncs)
			sumIC += c.getIC(conf.getICconf(),v);
		
		double sim_grasm = sumIC/disjointAncs.size();
		
		
		if(conf != null && conf.containsParam(beta_param_name))
			beta = conf.getParamAsDouble(beta_param_name);
		
		
		return sim(ic_a, ic_b, sim_grasm ,beta);
	}

	
	
	public double sim(double ic_a, double ic_b, double ic_mica, double beta) throws SLIB_Ex_Critic {
		
		double den = (  (ic_a) + (ic_b) + (beta - 2.) * ic_mica );
		double j = 0.;
		
		if(den != 0)
			j = ( ( beta * ic_mica ) / den );

		return j;
	}	
	
	public static void main(String[] args) throws SLIB_Ex_Critic {
		Sim_pairwise_DAG_node_GL_GraSM p = new Sim_pairwise_DAG_node_GL_GraSM();
		System.out.println(p.sim(0.25, 0.25, 0.20, 0));
	}
}
