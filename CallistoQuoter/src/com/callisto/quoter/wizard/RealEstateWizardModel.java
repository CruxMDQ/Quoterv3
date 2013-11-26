package com.callisto.quoter.wizard;

import android.content.Context;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.MultipleFixedChoicePage;
import com.example.android.wizardpager.wizard.model.PageList;
import com.example.android.wizardpager.wizard.model.SingleFixedChoicePage;

// Original template: WizardPagerSource/SandwichWizardModel
public class RealEstateWizardModel extends AbstractWizardModel
{

	public RealEstateWizardModel(Context context)
	{
		super(context);
	}

	@Override
	protected PageList onNewRootPageList()
	{
		return new PageList(
				new SingleFixedChoicePage(this, "Tipo de propiedad").setChoices(
						"Local", "Galp—n", "Chalet", "Dœplex", "Departamento",
						"PH"),

				new MultipleFixedChoicePage(this, "Tipo de operaci—n")
						.setChoices("Venta", "Alquiler residencial",
								"Alquiler comercial", "Alquiler vacacional"));

	}

}
