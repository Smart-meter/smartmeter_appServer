"""Pipeline configuration.

This module loads configuration information from files in the config directory.
"""
import pathlib
from omegaconf import OmegaConf
from ml_pipeline import utils
class Config:
    """ Pipeline configuration."""
    def __init__(self, config_dir: str, project: str)->None:
        """
        Instantiates the config object.
        :param config_dir: Path to config directory.
        :param project: Project name. Must correspond to a YAML file.
        :return: None
        """
        self.logger = utils.Logger("config", debug=True).get()
        self.logger.debug(f"Initializing config object with {config_dir} as config directory and {project} as project")
        self.config_dir = pathlib.Path(config_dir)
        self.project = project

    def load(self) -> None:
        """
        Loads configuration into the config object

        :return:
        """
        self.items = OmegaConf.merge(
            OmegaConf.load(self.config_dir / "common.yaml"),
            OmegaConf.load(self.config_dir / "datasets.yaml"),
            OmegaConf.load(self.config_dir / f"{self.project}.yaml"),
        )
    def __repr__(self):
        """
        Printable representation of an object of this type
        :return:
        """
        return OmegaConf.to_yaml(self.items)



