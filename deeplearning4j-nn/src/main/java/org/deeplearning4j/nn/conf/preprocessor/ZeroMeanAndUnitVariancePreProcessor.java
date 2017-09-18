/*-
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package org.deeplearning4j.nn.conf.preprocessor;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.deeplearning4j.nn.api.activations.Activations;
import org.deeplearning4j.nn.api.activations.ActivationsFactory;
import org.deeplearning4j.nn.api.gradients.Gradients;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Zero mean and unit variance operation
 *
 * @author Adam Gibson
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ZeroMeanAndUnitVariancePreProcessor extends BaseInputPreProcessor {


    @Override
    public Activations preProcess(Activations a, int miniBatchSize) {
        Activations out = ActivationsFactory.getInstance().create(a.getAsArray(),
                a.getMaskAsArray(), a.getMaskStateAsArray());
        for(int i=0; i<a.size(); i++ ) {
            INDArray input = a.get(i);
            INDArray columnMeans = input.mean(0);
            INDArray columnStds = input.std(0);
            input.subiRowVector(columnMeans);
            columnStds.addi(Nd4j.EPS_THRESHOLD);
            input.diviRowVector(columnStds);
        }

        return out;
    }

    @Override
    public Gradients backprop(Gradients g, int miniBatchSize) {
        return g; //no-op
    }

    @Override
    public InputType getOutputType(InputType inputType) {
        if (inputType == null)
            throw new IllegalStateException("Invalid input type: cannot be null");
        return inputType;
    }

}
