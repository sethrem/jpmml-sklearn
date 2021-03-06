/*
 * Copyright (c) 2015 Villu Ruusmann
 *
 * This file is part of JPMML-SkLearn
 *
 * JPMML-SkLearn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-SkLearn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-SkLearn.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.sklearn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import joblib.NDArrayWrapperConstructor;
import joblib.NumpyArrayWrapper;
import net.razorvine.pickle.Opcodes;
import net.razorvine.pickle.Unpickler;
import numpy.DType;
import numpy.core.NDArray;
import numpy.core.Scalar;
import numpy.core.UFunc;
import numpy.random.RandomState;
import scipy.sparse.CSRMatrix;
import sklearn.cluster.KMeans;
import sklearn.cluster.MiniBatchKMeans;
import sklearn.decomposition.IncrementalPCA;
import sklearn.decomposition.PCA;
import sklearn.discriminant_analysis.LinearDiscriminantAnalysis;
import sklearn.ensemble.bagging.BaggingClassifier;
import sklearn.ensemble.bagging.BaggingRegressor;
import sklearn.ensemble.forest.ExtraTreesClassifier;
import sklearn.ensemble.forest.ExtraTreesRegressor;
import sklearn.ensemble.forest.RandomForestClassifier;
import sklearn.ensemble.forest.RandomForestRegressor;
import sklearn.ensemble.gradient_boosting.BinomialDeviance;
import sklearn.ensemble.gradient_boosting.ExponentialLoss;
import sklearn.ensemble.gradient_boosting.GradientBoostingClassifier;
import sklearn.ensemble.gradient_boosting.GradientBoostingRegressor;
import sklearn.ensemble.gradient_boosting.LogOddsEstimator;
import sklearn.ensemble.gradient_boosting.MeanEstimator;
import sklearn.ensemble.gradient_boosting.MultinomialDeviance;
import sklearn.ensemble.gradient_boosting.PriorProbabilityEstimator;
import sklearn.ensemble.gradient_boosting.QuantileEstimator;
import sklearn.ensemble.gradient_boosting.ScaledLogOddsEstimator;
import sklearn.ensemble.gradient_boosting.ZeroEstimator;
import sklearn.ensemble.voting_classifier.VotingClassifier;
import sklearn.linear_model.LinearRegression;
import sklearn.linear_model.LogisticRegression;
import sklearn.linear_model.coordinate_descent.ElasticNet;
import sklearn.linear_model.coordinate_descent.Lasso;
import sklearn.linear_model.ridge.Ridge;
import sklearn.linear_model.ridge.RidgeClassifier;
import sklearn.linear_model.stochastic_gradient.Hinge;
import sklearn.linear_model.stochastic_gradient.Log;
import sklearn.linear_model.stochastic_gradient.ModifiedHuber;
import sklearn.linear_model.stochastic_gradient.SGDClassifier;
import sklearn.linear_model.stochastic_gradient.SGDRegressor;
import sklearn.linear_model.stochastic_gradient.SquaredHinge;
import sklearn.naive_bayes.GaussianNB;
import sklearn.neighbors.BinaryTree;
import sklearn.neighbors.DistanceMetric;
import sklearn.neighbors.KNeighborsClassifier;
import sklearn.neighbors.KNeighborsRegressor;
import sklearn.neural_network.MLPClassifier;
import sklearn.neural_network.MLPRegressor;
import sklearn.preprocessing.Binarizer;
import sklearn.preprocessing.FunctionTransformer;
import sklearn.preprocessing.Imputer;
import sklearn.preprocessing.LabelBinarizer;
import sklearn.preprocessing.LabelEncoder;
import sklearn.preprocessing.MaxAbsScaler;
import sklearn.preprocessing.MinMaxScaler;
import sklearn.preprocessing.OneHotEncoder;
import sklearn.preprocessing.RobustScaler;
import sklearn.preprocessing.StandardScaler;
import sklearn.svm.LinearSVR;
import sklearn.svm.NuSVC;
import sklearn.svm.NuSVR;
import sklearn.svm.OneClassSVM;
import sklearn.svm.SVC;
import sklearn.svm.SVR;
import sklearn.tree.DecisionTreeClassifier;
import sklearn.tree.DecisionTreeRegressor;
import sklearn.tree.ExtraTreeClassifier;
import sklearn.tree.ExtraTreeRegressor;
import sklearn.tree.PresortBestSplitter;
import sklearn.tree.RegressionCriterion;
import sklearn.tree.Tree;
import sklearn2pmml.decoration.CategoricalDomain;
import sklearn2pmml.decoration.ContinuousDomain;
import sklearn_pandas.DataFrameMapper;
import sklearn_pandas.TransformerPipeline;
import xgboost.sklearn.Booster;
import xgboost.sklearn.XGBClassifier;
import xgboost.sklearn.XGBRegressor;

public class PickleUtil {

	private PickleUtil(){
	}

	static
	public Storage createStorage(File file){

		try {
			InputStream is = new FileInputStream(file);

			try {
				return new CompressedInputStreamStorage(is);
			} catch(IOException ioe){
				is.close();
			}
		} catch(IOException ioe){
			// Ignored
		}

		return new FileStorage(file);
	}

	static
	public Object unpickle(Storage storage) throws IOException {
		ObjectConstructor[] constructors = {
			new NDArrayWrapperConstructor("joblib.numpy_pickle", "NDArrayWrapper", storage),
			new ObjectConstructor("joblib.numpy_pickle", "NumpyArrayWrapper", NumpyArrayWrapper.class),
			new ExtensionObjectConstructor("numpy", "dtype", DType.class),
			new ExtensionObjectConstructor("numpy.core", "_ufunc_reconstruct", UFunc.class),
			new ExtensionObjectConstructor("numpy.core.multiarray", "_reconstruct", NDArray.class),
			new ExtensionObjectConstructor("numpy.core.multiarray", "scalar", Scalar.class),
			new ExtensionObjectConstructor("numpy.random", "__RandomState_ctor", RandomState.class),
			new ObjectConstructor("scipy.sparse.csr", "csr_matrix", CSRMatrix.class),
			new ObjectConstructor("sklearn.cluster.k_means_", "KMeans", KMeans.class),
			new ObjectConstructor("sklearn.cluster.k_means_", "MiniBatchKMeans", MiniBatchKMeans.class),
			new ObjectConstructor("sklearn.decomposition.incremental_pca", "IncrementalPCA", IncrementalPCA.class),
			new ObjectConstructor("sklearn.decomposition.pca", "PCA", PCA.class),
			new ObjectConstructor("sklearn.discriminant_analysis", "LinearDiscriminantAnalysis", LinearDiscriminantAnalysis.class),
			new ObjectConstructor("sklearn.ensemble.bagging", "BaggingClassifier", BaggingClassifier.class),
			new ObjectConstructor("sklearn.ensemble.bagging", "BaggingRegressor", BaggingRegressor.class),
			new ObjectConstructor("sklearn.ensemble.forest", "ExtraTreesClassifier", ExtraTreesClassifier.class),
			new ObjectConstructor("sklearn.ensemble.forest", "ExtraTreesRegressor", ExtraTreesRegressor.class),
			new ObjectConstructor("sklearn.ensemble.forest", "RandomForestClassifier", RandomForestClassifier.class),
			new ObjectConstructor("sklearn.ensemble.forest", "RandomForestRegressor", RandomForestRegressor.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "BinomialDeviance", BinomialDeviance.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "ExponentialLoss", ExponentialLoss.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "GradientBoostingClassifier", GradientBoostingClassifier.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "GradientBoostingRegressor", GradientBoostingRegressor.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "LogOddsEstimator", LogOddsEstimator.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "MeanEstimator", MeanEstimator.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "MultinomialDeviance", MultinomialDeviance.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "PriorProbabilityEstimator", PriorProbabilityEstimator.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "QuantileEstimator", QuantileEstimator.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "ScaledLogOddsEstimator", ScaledLogOddsEstimator.class),
			new ObjectConstructor("sklearn.ensemble.gradient_boosting", "ZeroEstimator", ZeroEstimator.class),
			new ObjectConstructor("sklearn.ensemble.voting_classifier", "VotingClassifier", VotingClassifier.class),
			new NDArrayWrapperConstructor("sklearn.externals.joblib.numpy_pickle", "NDArrayWrapper", storage),
			new ObjectConstructor("sklearn.externals.joblib.numpy_pickle", "NumpyArrayWrapper", NumpyArrayWrapper.class),
			new ObjectConstructor("sklearn.linear_model.base", "LinearRegression", LinearRegression.class),
			new ObjectConstructor("sklearn.linear_model.coordinate_descent", "ElasticNet", ElasticNet.class),
			new ObjectConstructor("sklearn.linear_model.coordinate_descent", "ElasticNetCV", ElasticNet.class),
			new ObjectConstructor("sklearn.linear_model.coordinate_descent", "Lasso", Lasso.class),
			new ObjectConstructor("sklearn.linear_model.coordinate_descent", "LassoCV", Lasso.class),
			new ObjectConstructor("sklearn.linear_model.logistic", "LogisticRegression", LogisticRegression.class),
			new ObjectConstructor("sklearn.linear_model.logistic", "LogisticRegressionCV", LogisticRegression.class),
			new ObjectConstructor("sklearn.linear_model.ridge", "Ridge", Ridge.class),
			new ObjectConstructor("sklearn.linear_model.ridge", "RidgeCV", Ridge.class),
			new ObjectConstructor("sklearn.linear_model.ridge", "RidgeClassifier", RidgeClassifier.class),
			new ObjectConstructor("sklearn.linear_model.ridge", "RidgeClassifierCV", RidgeClassifier.class),
			new ExtensionObjectConstructor("sklearn.linear_model.sgd_fast", "Hinge", Hinge.class),
			new ExtensionObjectConstructor("sklearn.linear_model.sgd_fast", "Log", Log.class),
			new ExtensionObjectConstructor("sklearn.linear_model.sgd_fast", "ModifiedHuber", ModifiedHuber.class),
			new ExtensionObjectConstructor("sklearn.linear_model.sgd_fast", "SquaredHinge", SquaredHinge.class),
			new ObjectConstructor("sklearn.linear_model.stochastic_gradient", "SGDClassifier", SGDClassifier.class),
			new ObjectConstructor("sklearn.linear_model.stochastic_gradient", "SGDRegressor", SGDRegressor.class),
			new ObjectConstructor("sklearn.naive_bayes", "GaussianNB", GaussianNB.class),
			new ObjectConstructor("sklearn.neighbors.classification", "KNeighborsClassifier", KNeighborsClassifier.class),
			new ExtensionObjectConstructor("sklearn.neighbors.dist_metrics", "newObj", DistanceMetric.class),
			new ExtensionObjectConstructor("sklearn.neighbors.kd_tree", "newObj", BinaryTree.class),
			new ObjectConstructor("sklearn.neighbors.regression", "KNeighborsRegressor", KNeighborsRegressor.class),
			new ObjectConstructor("sklearn.neural_network.multilayer_perceptron", "MLPClassifier", MLPClassifier.class),
			new ObjectConstructor("sklearn.neural_network.multilayer_perceptron", "MLPRegressor", MLPRegressor.class),
			new ObjectConstructor("sklearn.preprocessing._function_transformer", "FunctionTransformer", FunctionTransformer.class),
			new ObjectConstructor("sklearn.preprocessing.data", "Binarizer", Binarizer.class),
			new ObjectConstructor("sklearn.preprocessing.data", "MaxAbsScaler", MaxAbsScaler.class),
			new ObjectConstructor("sklearn.preprocessing.data", "MinMaxScaler", MinMaxScaler.class),
			new ObjectConstructor("sklearn.preprocessing.data", "OneHotEncoder", OneHotEncoder.class),
			new ObjectConstructor("sklearn.preprocessing.data", "RobustScaler", RobustScaler.class),
			new ObjectConstructor("sklearn.preprocessing.data", "StandardScaler", StandardScaler.class),
			new ObjectConstructor("sklearn.preprocessing.imputation", "Imputer", Imputer.class),
			new ObjectConstructor("sklearn.preprocessing.label", "LabelBinarizer", LabelBinarizer.class),
			new ObjectConstructor("sklearn.preprocessing.label", "LabelEncoder", LabelEncoder.class),
			new ObjectConstructor("sklearn.svm.classes", "LinearSVR", LinearSVR.class),
			new ObjectConstructor("sklearn.svm.classes", "NuSVC", NuSVC.class),
			new ObjectConstructor("sklearn.svm.classes", "NuSVR", NuSVR.class),
			new ObjectConstructor("sklearn.svm.classes", "OneClassSVM", OneClassSVM.class),
			new ObjectConstructor("sklearn.svm.classes", "SVC", SVC.class),
			new ObjectConstructor("sklearn.svm.classes", "SVR", SVR.class),
			new ExtensionObjectConstructor("sklearn.tree._tree", "BestSplitter"),
			new ExtensionObjectConstructor("sklearn.tree._tree", "ClassificationCriterion"),
			new ExtensionObjectConstructor("sklearn.tree._tree", "PresortBestSplitter", PresortBestSplitter.class),
			new ExtensionObjectConstructor("sklearn.tree._tree", "RegressionCriterion", RegressionCriterion.class),
			new ExtensionObjectConstructor("sklearn.tree._tree", "Tree", Tree.class),
			new ObjectConstructor("sklearn.tree.tree", "DecisionTreeClassifier", DecisionTreeClassifier.class),
			new ObjectConstructor("sklearn.tree.tree", "DecisionTreeRegressor", DecisionTreeRegressor.class),
			new ObjectConstructor("sklearn.tree.tree", "ExtraTreeClassifier", ExtraTreeClassifier.class),
			new ObjectConstructor("sklearn.tree.tree", "ExtraTreeRegressor", ExtraTreeRegressor.class),
			new ObjectConstructor("sklearn_pandas", "DataFrameMapper", DataFrameMapper.class), // sklearn_pandas 0.X
			new ObjectConstructor("sklearn_pandas.dataframe_mapper", "DataFrameMapper", DataFrameMapper.class), // sklearn_pandas 1.X
			new ObjectConstructor("sklearn_pandas.pipeline", "TransformerPipeline", TransformerPipeline.class),
			new ObjectConstructor("sklearn2pmml.decoration", "CategoricalDomain", CategoricalDomain.class),
			new ObjectConstructor("sklearn2pmml.decoration", "ContinuousDomain", ContinuousDomain.class),
			new ObjectConstructor("xgboost.core", "Booster", Booster.class),
			new ObjectConstructor("xgboost.sklearn", "XGBClassifier", XGBClassifier.class),
			new ObjectConstructor("xgboost.sklearn", "XGBRegressor", XGBRegressor.class),
		};

		for(ObjectConstructor constructor : constructors){
			Unpickler.registerConstructor(constructor.getModule(), constructor.getName(), constructor);
		}

		try(final InputStream is = storage.getObject()){
			Unpickler unpickler = new Unpickler(){

				@Override
				protected Object dispatch(short key) throws IOException {
					Object result = super.dispatch(key);

					if(key == Opcodes.BUILD){
						Object head = super.stack.peek();

						// Modify the stack by replacing NumpyArrayWrapper with NDArray
						if(head instanceof NumpyArrayWrapper){
							NumpyArrayWrapper arrayWrapper = (NumpyArrayWrapper)head;

							super.stack.pop();

							NDArray array = arrayWrapper.toArray(is);

							super.stack.add(array);
						}
					}

					return result;
				}
			};

			return unpickler.load(is);
		}
	}
}