from ml_pipeline.datasets import amr
class DatasetFactory:
    def __init__(self, dataset_config, artifact_dir, logger):
        self.datasets = {
            "amr": {"class": amr.AMRDataset}
        }
        #For the datasets in the configuration path, register the datasets\
        for dataset, config in dataset_config.items():
            if dataset in self.datasets:
                self.datasets[dataset]["path"] = config.path
        self.artifact_dir = artifact_dir
        self.logger = logger

        def get(self, name: str):
            """
            Return the appropriate Dataset object for the dataset passed
            :param self:
            :param name:
            :return:
            """
            return self.datasets[name]["class"](
                "data/" + self.datasets[name]["path"],
                self.artifact_dir,
                self.logger,
            )