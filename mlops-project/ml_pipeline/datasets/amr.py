from ml_pipeline.dataset import Dataset
class AMRDataset(Dataset):

    def __init__(
            self,
            data_path: str = None,
            artifact_dir: str = None,
            logger: "logging.Logger" = None,
    ) -> None:
        """Instantiates the dataset object.

        Args:
            data_path (str): Path to the folder with images
        """
        self.name = "amr"
        self.data_path = data_path
        self.artifact_dir = artifact_dir
        self.logger = logger

    def load(self) -> None:
        pass

    def preprocess(self) -> None:
        pass

    def feature_engineer(self) -> None:
        pass

    def save(self) -> None:
        pass
