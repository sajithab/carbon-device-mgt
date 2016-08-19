/*
*  Copyright (c) 2015 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.policy.evaluator;

import org.wso2.carbon.policy.evaluator.utils.Constants;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.common.ProfileFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for evaluating the policy (Configurations sets) and returning
 * the effective features set.
 */

public class FeatureFilterImpl implements FeatureFilter {

    /**
     * This method returns the effective feature list when policy list and feature aggregation rules are supplied.
     * @param policyList
     * @param featureRulesList
     * @return
     */
    @Override
    public List<ProfileFeature> evaluate(List<Policy> policyList, List<FeatureRules> featureRulesList) {
        return evaluateFeatures(extractFeatures(policyList), featureRulesList);
     }

    /**
     * This method extract the features from the given policy list in the order they are provided in the list.
     * @param policyList
     * @return
     */
    public List<ProfileFeature> extractFeatures(List<Policy> policyList) {
        List<ProfileFeature> featureList = new ArrayList<ProfileFeature>();
        for (Policy policy : policyList) {
            featureList.addAll(policy.getProfile().getProfileFeaturesList());
        }
        return featureList;
    }

    /**
     * This method is responsible for supplying tasks to other methods to evaluate given features.
     * @param featureList
     * @param featureRulesList
     * @return
     */
    public List<ProfileFeature> evaluateFeatures(List<ProfileFeature> featureList, List<FeatureRules> featureRulesList) {
        List<ProfileFeature> effectiveFeatureList = new ArrayList<ProfileFeature>();
        for (FeatureRules rule : featureRulesList) {
            String ruleName = rule.getEvaluationCriteria();
            String featureName = rule.getName();
            if (Constants.DENY_OVERRIDES.equalsIgnoreCase(ruleName)) {
                getDenyOverridesFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (Constants.PERMIT_OVERRIDES.equalsIgnoreCase(ruleName)) {
                getPermitOverridesFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (Constants.FIRST_APPLICABLE.equalsIgnoreCase(ruleName)) {
                getFirstApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (Constants.LAST_APPLICABLE.equalsIgnoreCase(ruleName)) {
                getLastApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (Constants.ALL_APPLICABLE.equalsIgnoreCase(ruleName)) {
                getAllApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (Constants.HIGHEST_APPLICABLE.equalsIgnoreCase(ruleName)) {
                getHighestApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (Constants.LOWEST_APPLICABLE.equalsIgnoreCase(ruleName)) {
                getLowestApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
        }
        return effectiveFeatureList;
    }

    /**
     * This method picks up denied features, if there is no denied features it will add to the list, the final permitted feature.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getDenyOverridesFeatures(String featureName, List<ProfileFeature> featureList, List<ProfileFeature> effectiveFeatureList) {
        ProfileFeature evaluatedFeature = null;
//        for (ProfileFeature feature : featureList) {
//            if (feature.getFeature().getName().equalsIgnoreCase(featureName)) {
//                if (feature.getFeature().getRuleValue().equalsIgnoreCase("Deny")) {
//                    evaluatedFeature = feature;
//                    effectiveFeatureList.add(evaluatedFeature);
//                    return;
//                } else {
//                    evaluatedFeature = feature;
//                }
//            }
//        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }

    }

    /**
     * This method picks up permitted features, if there is no permitted features it will add to the list, the final denied feature.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getPermitOverridesFeatures(String featureName, List<ProfileFeature> featureList, List<ProfileFeature> effectiveFeatureList) {
        ProfileFeature evaluatedFeature = null;
//        for (ProfileFeature feature : featureList) {
//            if (feature.getFeature().getName().equalsIgnoreCase(featureName)) {
//                if (feature.getFeature().getRuleValue().equalsIgnoreCase("Permit")) {
//                    evaluatedFeature = feature;
//                    effectiveFeatureList.add(evaluatedFeature);
//                    return;
//                } else {
//                    evaluatedFeature = feature;
//                }
//            }
//        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }

    }

    /**
     * This method picks the first features of the give type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getFirstApplicableFeatures(String featureName, List<ProfileFeature> featureList, List<ProfileFeature> effectiveFeatureList) {
        for (ProfileFeature feature : featureList) {
//            if (feature.getFeature().getName().equalsIgnoreCase(featureName)) {
//                effectiveFeatureList.add(feature);
//                return;
//
//            }
        }
    }

    /**
     * This method picks the last features of the give type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getLastApplicableFeatures(String featureName, List<ProfileFeature> featureList, List<ProfileFeature> effectiveFeatureList) {
        ProfileFeature evaluatedFeature = null;
//        for (ProfileFeature feature : featureList) {
//            if (feature.getFeature().getName().equalsIgnoreCase(featureName)) {
//                evaluatedFeature = feature;
//            }
//        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }
    }

    /**
     * This method picks the all features of the give type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getAllApplicableFeatures(String featureName, List<ProfileFeature> featureList, List<ProfileFeature> effectiveFeatureList) {
        for (ProfileFeature feature : featureList) {
//            if (feature.getFeature().getName().equalsIgnoreCase(featureName)) {
//                effectiveFeatureList.add(feature);
//            }
        }
    }

    /**
     * This method picks the feature with the highest value of given type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getHighestApplicableFeatures(String featureName, List<ProfileFeature> featureList, List<ProfileFeature> effectiveFeatureList) {
        ProfileFeature evaluatedFeature = null;
        int intValve = 0;
//        for (ProfileFeature feature : featureList) {
//            if (feature.getFeature().getName().equalsIgnoreCase(featureName)) {
//                if (Integer.parseInt(feature.getFeature().getRuleValue()) > intValve) {
//                    intValve = Integer.parseInt(feature.getFeature().getRuleValue());
//                    evaluatedFeature = feature;
//                }
//            }
//        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }
    }

    /**
     * This method picks the feature with the lowest value of given type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getLowestApplicableFeatures(String featureName, List<ProfileFeature> featureList, List<ProfileFeature> effectiveFeatureList) {
        ProfileFeature evaluatedFeature = null;
//        int intValve = 0;
//        for (ProfileFeature feature : featureList) {
//            if (feature.getFeature().getName().equalsIgnoreCase(featureName)) {
//                if (Integer.parseInt(feature.getFeature().getRuleValue()) < intValve) {
//                    intValve = Integer.parseInt(feature.getFeature().getRuleValue());
//                    evaluatedFeature = feature;
//                }
//            }
//        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }
    }
}
